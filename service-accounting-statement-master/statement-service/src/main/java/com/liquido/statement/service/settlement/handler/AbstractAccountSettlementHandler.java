package com.liquido.statement.service.settlement.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.persistence.EntityManager;

import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VersionEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountStatementService;
import com.liquido.statement.service.TransactionFeeService;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.settlement.AccountSettlementHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract Account SettlementHandler
 */
@Slf4j
@Service
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractAccountSettlementHandler implements AccountSettlementHandler {
    @Resource
    private EntityManager entityManager;
    @Resource
    private AccountService accountService;
    @Resource
    private TransactionFeeService transactionFeeService;
    @Resource
    private TransactionMoneyService transactionMoneyService;
    @Resource
    private AccountStatementService accountStatementService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void executeAccountSettlement(final BatchAccountSettlementBo batchBo) {

        /* PreExecute account settlement */
        this.prepareExecuteSettlement(batchBo);

        /* Executing account settlement */
        this.executingAccountSettlement(batchBo);

        /* Batch update settlement status */
        this.batchUpdateOrderStatus(batchBo);

        /* PostExecute account settlement */
        this.postExecuteSettlement(batchBo);
    }

    /**
     * executing account settlement
     *
     * @param batchBo batchBo
     */
    protected void executingAccountSettlement(final BatchAccountSettlementBo batchBo) {
        // reload account Info;
        final Account account = accountService.getById(batchBo.getAccountSnapshot().getId());

        // Calculate settlement amount;
        final BigDecimal occurredAmount = this.calculateSettlementAmount(batchBo);

        // Calculate the pay_in account immediate liquidation(T+0 OR D+0) extractable amount,
        // and the non-immediate liquidation is calculated after the daily cutoff completed;
        // Other account's(e.g: payout, marketplace) Realtime update the extractable balance;
        final BigDecimal extractableAmount = this.calculateExtractableAmount(
                account, occurredAmount, batchBo);

        /* Operating account amount */
        if (!accountService.increaseAccountBalance(account, occurredAmount, extractableAmount,
                batchBo.getTransactionMoneyList().size())) {
            throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
        }

        /* Batch generate and save account change flow */
        accountStatementService.batchGenerateAndSaveSettlementFlow(account, batchBo);

        /* This step is very important,
            when the for loop updates the account in the same transaction
            to get the latest optimistic lock version number
         */
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Calculate settlement amount
     *
     * @param batchBo BatchAccountSettlementBo
     */
    private BigDecimal calculateSettlementAmount(final BatchAccountSettlementBo batchBo) {

        final BigDecimal totalSettlementAmount = batchBo.getTransactionMoneyList().stream()
                .filter(x -> Objects.nonNull(x.getSettlementAmount())
                        && Objects.nonNull(x.getAmountPon()))
                .map(o -> o.getSettlementAmount().multiply(o.getAmountPon()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalAdditionalAmount = batchBo.getTransactionMoneyList().stream()
                .filter(x -> CollectionUtils.isNotEmpty(x.getAdditionalCharge()))
                .flatMap(x -> x.getAdditionalCharge().stream()
                        .filter(ac -> Objects.nonNull(ac.getAmount()))
                        .map(ac -> ac.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalTransactionFee = batchBo.getTransactionFeeList().stream()
                .filter(item -> Objects.nonNull(item.getInstantFlag())
                        && item.getInstantFlag()
                        && Objects.nonNull(item.getSettlementAmount())
                        && Objects.nonNull(item.getAmountPon()))
                .map(o -> o.getSettlementAmount().multiply(o.getAmountPon()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalSettlementAmount.add(totalAdditionalAmount).add(totalTransactionFee);
    }

    /**
     * Calculate extractable amount
     *
     * @param account
     * @param occurredAmount
     * @param batchBo
     *
     * @return
     */
    private BigDecimal calculateExtractableAmount(
            final Account account,
            final BigDecimal occurredAmount,
            final BatchAccountSettlementBo batchBo) {

        // Other account's(e.g: payout, marketplace) Real time update of extractable balance;
        if (TransactionTypeCodeEnum.PAY_IN != account.getTransactionTypeCode()) {
            return occurredAmount;
        }

        // Statistics PAY_IN(T+n OR D+n) Not yet accounted for.
        final BigDecimal originalOrderBeCreditedAmount = batchBo.getNeedCreditOriginalMoneyList()
                .stream().map(TransactionMoney::getBeCreditedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Statistics PAY_IN(T+0 OR D+0) extractable amount
        return batchBo.getTransactionMoneyList().stream()
                .filter(x -> TradingModelEnum.INSTANT_TRADING.contains(x.getTradingModel())
                        && HoldStatusEnum.NORMAL == x.getHoldStatus())
                .map(TransactionMoney::getBeCreditedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(originalOrderBeCreditedAmount);
    }

    /**
     * after process account settlement
     *
     * @param batchBo BatchAccountSettlementBo
     */
    private void batchUpdateOrderStatus(final BatchAccountSettlementBo batchBo) {
        // Update settleStatus from PROCESSING to SUCCESS
        final List<Long> moneyIdList =
                batchBo.getTransactionMoneyList().stream().map(TransactionMoney::getId)
                        .collect(Collectors.toList());

        transactionMoneyService.batchUpdateState(moneyIdList, SettleStatusEnum.PROCESSING,
                SettleStatusEnum.SUCCESS, VersionEnum.LOCKED.getCode());

        final List<Long> feeIdList =
                batchBo.getTransactionFeeList().stream().filter(TransactionFee::getInstantFlag)
                        .map(TransactionFee::getId).collect(Collectors.toList());

        transactionFeeService.batchUpdateState(feeIdList, SettleStatusEnum.PROCESSING,
                SettleStatusEnum.SUCCESS, VersionEnum.LOCKED.getCode());
    }
}
