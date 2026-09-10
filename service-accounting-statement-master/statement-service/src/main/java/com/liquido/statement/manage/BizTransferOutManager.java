package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountStatementBizBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountStatementBizService;
import com.liquido.statement.service.AccountStatementService;
import com.liquido.statement.service.TransactionBizService;
import com.liquido.statement.service.TransactionCostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizTransferOutManager {
    private final BaseService baseService;
    private final RedisDistLock redisDistLock;
    private final AccountService accountService;
    private final TransactionBizService transactionBizService;
    private final AccountStatementService accountStatementService;
    private final TransactionCostService transactionCostService;
    private final AccountStatementBizService accountStatementBizService;

    @Transactional(rollbackFor = Throwable.class)
    public void approvedPass(final TransactionBizVo order) {
        try {
            /* Step1: data pre-check repeat request (prevent duplicate submissions) */
            this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                    order.getRequestId(), 1, TimeUnit.DAYS);

            order.setAmountPon(AmountPonEnum.NEGATIVE);
            order.setOperateMode(OperateModeEnum.MANUAL);
            /* Step2: locked account */
            final Account account = accountService.getAccountLocked(
                    order.getAccountId(),
                    order.getRequestId());
            final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());

            if (account.getCurrency() != order.getSettlementCurrency()) {
                throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
            }

            // total transferOutAmt = settlementAmount
            final BigDecimal settleAmount = order.getSettlementAmount();
            final BigDecimal accountBalance = account.getLatestDailyBalance()
                    .add(account.getSubTotalAmount());

            /* Step3: check insufficient extractable balance */
            if (Objects.isNull(account.getFrozenAmount())
                    || account.getFrozenAmount().compareTo(settleAmount) < 0) {
                log.error("Insufficient frozenAmount, accountId={}, "
                                + "frozenAmount={}, settleAmount={}",
                        account.getId(), account.getFrozenAmount(), settleAmount);
                throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                        AmountUtil.centToYuan(account.getExtractableBalance()),
                        account.getCurrency());
            }

            order.setFeeAmount(Optional.ofNullable(order.getFeeAmount()).orElse(BigDecimal.ZERO)
                    .multiply(AmountPonEnum.NEGATIVE.getCode()));
            order.setFeeAmountUsd(Optional.ofNullable(order.getFeeAmountUsd())
                    .orElse(BigDecimal.ZERO).multiply(AmountPonEnum.NEGATIVE.getCode()));
            order.setTaxAmount(Optional.ofNullable(order.getTaxAmount()).orElse(BigDecimal.ZERO)
                    .multiply(AmountPonEnum.NEGATIVE.getCode()));
            order.setTaxAmountUsd(Optional.ofNullable(order.getTaxAmountUsd())
                    .orElse(BigDecimal.ZERO).multiply(AmountPonEnum.NEGATIVE.getCode()));

            /* Step4: save order */
            final TransactionBiz transferOut =
                    transactionBizService.saveTransactionBizOrder(account, order);

            /* Step5: update account balance */
            final BigDecimal endBalance = accountBalance.subtract(settleAmount);
            if (!accountService.reduceFrozenAmount(
                    order.getBusinessType(), account, settleAmount)) {
                throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
            }

            accountStatementBizService.saveAccountStatementBiz(
                    this.buildApprovalPassAccountStatementBiz(account, transferOut));

            /* Step6: save account statement flow*/
            accountStatementService.saveTransactionBizAccountFlow(
                    transferOut, settleAmount, accountBalance, endBalance);

            /* Step7: save biz transaction cost*/
            transactionCostService.saveBizTransactionCost(order, transferOut, merchant, account);

        } finally {
            /* Step7: release account lock*/
            accountService.releaseAccountLock(order.getAccountId(), order.getRequestId());
        }
    }

    /**
     * Account transferOut with account lock
     *
     * @param order
     */
    @Transactional(rollbackFor = Throwable.class)
    public void accountTransferOut(final TransactionBizVo order) {
        try {
            /* Step1: data pre-check repeat request (prevent duplicate submissions) */
            this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                    order.getRequestId(), 1, TimeUnit.DAYS);

            order.setOperateMode(OperateModeEnum.MANUAL);
            /* Step2: locked account */
            final Account account = accountService.getAccountLocked(
                    order.getAccountId(),
                    order.getRequestId());

            if (account.getCurrency() != order.getSettlementCurrency()) {
                throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
            }

            // total transferOutAmt = settlementAmount
            final BigDecimal settleAmount = order.getSettlementAmount();
            final BigDecimal accountBalance = account.getLatestDailyBalance()
                    .add(account.getSubTotalAmount());

            /* Step3: check insufficient balance */
            if (accountBalance.compareTo(BigDecimal.ZERO) <= 0
                    || accountBalance.compareTo(settleAmount) < 0) {
                log.error("Insufficient account balance, accountId={}, "
                                + "accountBalance={}, settleAmount={}",
                        account.getId(), accountBalance, settleAmount);
                throw StatementExceptionCode.INSUFFICIENT_ACCOUNT_BALANCE.exception();
            }

            /* Step4: execute transferOut */
            this.executeTransferOut(order, account);
        } finally {
            /* Step7: release account lock*/
            accountService.releaseAccountLock(order.getAccountId(), order.getRequestId());
        }
    }

    /**
     * Account transferOut with account non-lock
     *
     * @param order
     */
    @Transactional(rollbackFor = Throwable.class)
    public void executeTransferOut(final TransactionBizVo order, final Account account) {
        order.setAmountPon(AmountPonEnum.NEGATIVE);
        order.setBusinessType(BusinessTypeEnum.TRANSFER_OUT);
        order.setFeeAmount(Optional.ofNullable(order.getFeeAmount()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));
        order.setTaxAmount(Optional.ofNullable(order.getTaxAmount()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));

        /* Step1: save order */
        final TransactionBiz transferOut =
                transactionBizService.saveTransactionBizOrder(account, order);

        /* Step2: reduce account balance */
        final BigDecimal startBalance = account.getLatestDailyBalance()
                .add(account.getSubTotalAmount());
        final BigDecimal endBalance = startBalance.subtract(order.getSettlementAmount());
        if (!accountService.reduceAccountBalance(account, order.getSettlementAmount(), 1)) {
            throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
        }

        accountStatementBizService.saveAccountStatementBiz(
                this.buildAccountStatementBiz(account, transferOut));

        /* Step3: save account statement flow */
        accountStatementService.saveTransactionBizAccountFlow(
                transferOut,
                order.getSettlementAmount(),
                startBalance,
                endBalance);

        /* Step4: save biz transaction cost*/
        final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());
        transactionCostService.saveBizTransactionCost(order, transferOut, merchant, account);
    }

    private AccountStatementBizBo buildApprovalPassAccountStatementBiz(final Account account,
                                                                       final TransactionBiz biz) {
        return AccountStatementBizBo.builder()
                .requestId(biz.getRequestId())
                .transactionId(biz.getTransactionId())
                .merchantId(biz.getMerchantId())
                .subMerchantId(StringUtils.defaultIfBlank(biz.getSubMerchantId(),""))
                .accountId(biz.getAccountId())
                .billId(biz.getBillId())
                .businessType(biz.getBusinessType())
                .financeType(BizFinanceTypeEnum.TRANSACTION_DEAL)
                .transactionTime(biz.getTransactionTime())
                .extractableAmount(BigDecimal.ZERO)
                .frozenAmount(BusinessTypeEnum.TRANSFER_OUT == biz.getBusinessType()
                        ? biz.getSettlementAmount().multiply(biz.getAmountPon().getCode())
                        : BigDecimal.ZERO)
                .exchangeAmount(BusinessTypeEnum.EXCHANGE == biz.getBusinessType()
                        ? biz.getSettlementAmount().multiply(biz.getAmountPon().getCode())
                        : BigDecimal.ZERO)
                .currency(account.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(1)
                .delFlag(Boolean.FALSE)
                .remark(biz.getRemark())
                .build();
    }

    private AccountStatementBizBo buildAccountStatementBiz(final Account account,
                                                           final TransactionBiz biz) {
        return AccountStatementBizBo.builder()
                .requestId(biz.getRequestId())
                .transactionId(biz.getTransactionId())
                .merchantId(biz.getMerchantId())
                .subMerchantId(StringUtils.defaultIfBlank(biz.getSubMerchantId(),""))
                .accountId(biz.getAccountId())
                .billId(biz.getBillId())
                .businessType(biz.getBusinessType())
                .financeType(BizFinanceTypeEnum.TRANSACTION_DEAL)
                .transactionTime(biz.getTransactionTime())
                .extractableAmount(biz.getSettlementAmount()
                        .multiply(biz.getAmountPon().getCode()))
                .frozenAmount(BigDecimal.ZERO)
                .exchangeAmount(BigDecimal.ZERO)
                .currency(account.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(1)
                .delFlag(Boolean.FALSE)
                .remark(biz.getRemark())
                .build();
    }
}

