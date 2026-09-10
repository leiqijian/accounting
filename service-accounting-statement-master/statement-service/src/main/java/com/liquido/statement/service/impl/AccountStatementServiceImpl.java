package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountStatement;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.repository.AccountStatementRepository;
import com.liquido.statement.service.AccountStatementService;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountStatementServiceImpl implements AccountStatementService {
    private final AccountStatementRepository accountStatementRepository;

    @Override
    public void batchGenerateAndSaveSettlementFlow(final Account account,
                                                   final BatchAccountSettlementBo batchBo) {

        BigDecimal subTotalAmount = account.getSubTotalAmount();
        final List<AccountStatement> accountStatementList = Lists.newArrayList();
        for (final TransactionMoney money : batchBo.getTransactionMoneyList()) {

            final BigDecimal settleAmount =
                    money.getSettlementAmount().multiply(money.getAmountPon());

            final BigDecimal additionalAmount = Optional.ofNullable(money.getAdditionalCharge())
                    .orElse(Collections.emptyList()).stream()
                    .filter(x -> Objects.nonNull(x.getAmount()))
                    .map(ac -> ac.getAmount().multiply(money.getAmountPon()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            final BigDecimal feeAmount = batchBo.getTransactionFeeList().stream()
                    .filter(fee -> fee.getTransactionId().equals(money.getTransactionId())
                            && fee.getDirectionType().equals(money.getDirectionType())
                            && Objects.nonNull(fee.getSettlementAmount())
                            && Objects.nonNull(fee.getAmountPon()))
                    .map(o -> o.getSettlementAmount().multiply(o.getAmountPon()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            final BigDecimal netAmount = settleAmount.add(additionalAmount).add(feeAmount);

            final BigDecimal startBalance = account.getLatestDailyBalance().add(subTotalAmount);
            final BigDecimal endBalance = startBalance.add(netAmount);

            final AccountStatement accountStatement = this.buildTransactionFlow(
                    batchBo.getBusinessType(), money, netAmount, startBalance, endBalance);

            accountStatementList.add(accountStatement);

            // Accumulated amount
            subTotalAmount = subTotalAmount.add(netAmount);
        }

        this.batchGenerateAndSaveSettlementFlow(accountStatementList);
    }

    @Override
    public Long saveTransactionBizAccountFlow(final TransactionBiz biz,
                                              final BigDecimal settleAmount,
                                              final BigDecimal startBalance,
                                              final BigDecimal endBalance) {
        return this.saveAccountFlow(
                this.buildAccountStatement(biz, settleAmount, startBalance, endBalance));
    }

    @Override
    public AccountStatement buildAccountStatement(final TransactionBiz biz,
                                                  final BigDecimal settleAmount,
                                                  final BigDecimal startBalance,
                                                  final BigDecimal endBalance) {
        final AccountStatement accountStatement = new AccountStatement();
        accountStatement.setId(SnowflakeIdUtil.generate());
        accountStatement.setBusinessType(biz.getBusinessType());
        accountStatement.setTransactionId(biz.getTransactionId());
        accountStatement.setAccountId(biz.getAccountId());
        accountStatement.setBillId(biz.getBillId());
        accountStatement.setTransactionTypeCode(biz.getTransactionType());
        accountStatement.setProductCode(ProductCodeEnum.parse(biz.getPaymentChannel().getCode()));
        accountStatement.setDirectionType(DirectionTypeEnum.SETTLED);
        accountStatement.setAmount(settleAmount);
        accountStatement.setAmountPon(biz.getAmountPon());
        accountStatement.setCurrency(biz.getSettlementCurrency());
        accountStatement.setStartBalance(startBalance);
        accountStatement.setEndBalance(endBalance);
        accountStatement.setSubMerchantId(biz.getSubMerchantId());

        accountStatement.setTransactionTime(biz.getTransactionTime());
        accountStatement.setTransactionTimestamp(
                LocalDateTimeUtil.utcToInstant(biz.getTransactionTime()));
        accountStatement.setSettleTime(LocalDateTimeUtil.nowUtc());
        accountStatement.setCreatedTime(LocalDateTimeUtil.nowUtc());
        accountStatement.setUpdatedTime(LocalDateTimeUtil.nowUtc());
        accountStatement.setCreatedBy(0L);
        accountStatement.setUpdatedBy(0L);
        accountStatement.setVersion(1);
        accountStatement.setDelFlag(false);
        return accountStatement;
    }

    @Override
    public Long saveAccountFlow(final AccountStatement accountStatement) {
        return accountStatementRepository.save(accountStatement).getId();
    }

    @Override
    public List<AccountStatement> batchGenerateAndSaveSettlementFlow(
            final List<AccountStatement> accountStatements) {
        return accountStatementRepository.saveAllAndFlush(accountStatements);
    }

    /**
     * generate AccountChangeFlow pojo
     *
     * @param transactionMoney
     * @param settleAmount
     * @param startBalance
     * @param endBalance
     *
     * @return
     */
    private AccountStatement buildTransactionFlow(final BusinessTypeEnum businessType,
                                                  final TransactionMoney transactionMoney,
                                                  final BigDecimal settleAmount,
                                                  final BigDecimal startBalance,
                                                  final BigDecimal endBalance) {
        final AccountStatement accountStatement = new AccountStatement();
        accountStatement.setId(SnowflakeIdUtil.generate());
        accountStatement.setBusinessType(businessType);
        accountStatement.setTransactionId(transactionMoney.getTransactionId());
        accountStatement.setAccountId(transactionMoney.getAccountId());
        accountStatement.setBillId(transactionMoney.getBillId());
        accountStatement.setSubMerchantId(transactionMoney.getSubMerchantId());
        accountStatement.setTransactionTypeCode(transactionMoney.getTransactionTypeCode());
        accountStatement.setProductCode(transactionMoney.getProductCode());
        accountStatement.setDirectionType(transactionMoney.getDirectionType());

        if (settleAmount.compareTo(BigDecimal.ZERO) >= 0) {
            accountStatement.setAmount(settleAmount);
            accountStatement.setAmountPon(AmountPonEnum.POSITIVE);
        } else {
            // negative amount to positive
            accountStatement.setAmount(settleAmount.multiply(AmountPonEnum.NEGATIVE.getCode()));
            accountStatement.setAmountPon(AmountPonEnum.NEGATIVE);
        }

        accountStatement.setCurrency(transactionMoney.getSettlementCurrency());
        accountStatement.setStartBalance(startBalance);
        accountStatement.setEndBalance(endBalance);

        accountStatement.setTransactionTime(transactionMoney.getTransactionTime());
        accountStatement.setTransactionTimestamp(
                LocalDateTimeUtil.utcToInstant(transactionMoney.getTransactionTime()));
        accountStatement.setSettleTime(LocalDateTimeUtil.nowUtc());
        accountStatement.setCreatedTime(LocalDateTimeUtil.nowUtc());
        accountStatement.setUpdatedTime(LocalDateTimeUtil.nowUtc());
        accountStatement.setCreatedBy(0L);
        accountStatement.setUpdatedBy(0L);
        accountStatement.setVersion(1);
        accountStatement.setDelFlag(false);
        return accountStatement;
    }
}
