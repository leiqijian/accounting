package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.enums.AdjustmentRevenueRegardEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountStatementBizBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.vo.AdjustmentDeductionVo;
import com.liquido.statement.pojo.vo.AdjustmentReimburseVo;
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
public class BizAdjustmentManager {
    private final BaseService baseService;
    private final RedisDistLock redisDistLock;
    private final AccountService accountService;
    private final TransactionBizService transactionBizService;
    private final TransactionCostService transactionCostService;
    private final AccountStatementService accountStatementService;
    private final AccountStatementBizService accountStatementBizService;

    /**
     * account adjustment reimburse
     *
     * @param params
     */
    @Transactional(rollbackFor = Throwable.class)
    public void adjustmentReimburse(final AdjustmentReimburseVo params) {
        try {
            /* data pre-check repeat request (prevent duplicate submissions) */
            this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                    params.getRequestId(), 1, TimeUnit.DAYS);

            final Account account = accountService.getAccountLocked(
                    params.getAccountId(),
                    params.getRequestId());
            if (params.getMerchantId().longValue() != account.getMerchantId().longValue()) {
                throw StatementExceptionCode.UNKNOWN_ACCOUNT.exception();
            }

            final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());

            final TransactionBizVo bizVo = this.buildReimburseData(account, params);

            /* Step1: build transactionBiz order */
            final TransactionBiz order =
                    transactionBizService.saveTransactionBizOrder(account, bizVo);

            /* Step2: increase account balance */
            final BigDecimal startBalance =
                    account.getLatestDailyBalance().add(account.getSubTotalAmount());
            final BigDecimal endBalance = startBalance.add(order.getSettlementAmount());

            if (!accountService.increaseAccountBalance(
                    account, order.getSettlementAmount(), order.getSettlementAmount(), 1)) {
                throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
            }

            accountStatementBizService.saveAccountStatementBiz(
                    this.buildAccountStatementBiz(account, order));

            /* Step3: save account statement flow*/
            accountStatementService.saveTransactionBizAccountFlow(
                    order, order.getSettlementAmount(), startBalance, endBalance);

            /* Step4: save account adjustment cost*/
            transactionCostService.saveBizAdjustmentCost(merchant, account, order,
                    Optional.ofNullable(params.getRevenueRegard())
                            .orElse(AdjustmentRevenueRegardEnum.NONE));
        } finally {
            accountService.releaseAccountLock(params.getAccountId(), params.getRequestId());
        }
    }

    /**
     * account adjustment deduction
     *
     * @param params
     */
    @Transactional(rollbackFor = Throwable.class)
    public void adjustmentDeduction(final AdjustmentDeductionVo params) {
        try {
            /* data pre-check repeat request (prevent duplicate submissions) */
            this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                    params.getRequestId(), 3, TimeUnit.DAYS);

            final Account account = accountService.getAccountLocked(
                    params.getAccountId(),
                    params.getRequestId());
            if (params.getMerchantId().longValue() != account.getMerchantId().longValue()) {
                throw StatementExceptionCode.UNKNOWN_ACCOUNT.exception();
            }

            final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());


            final TransactionBizVo bizVo = this.buildDeductionData(account, params);

            /* Step1: build transactionBiz deduction */
            final TransactionBiz order =
                    transactionBizService.saveTransactionBizOrder(account, bizVo);

            /* Step2: reduce account balance */
            final BigDecimal startBalance = account.getLatestDailyBalance()
                    .add(account.getSubTotalAmount());
            final BigDecimal endBalance = startBalance.subtract(order.getSettlementAmount());
            if (!accountService.reduceAccountBalance(account, order.getSettlementAmount(), 1)) {
                throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
            }

            accountStatementBizService.saveAccountStatementBiz(
                    this.buildAccountStatementBiz(account, order));

            /* Step3: save account statement flow*/
            accountStatementService.saveTransactionBizAccountFlow(
                    order, order.getSettlementAmount(), startBalance, endBalance);

            /* Step4: save account adjustment cost*/
            transactionCostService.saveBizAdjustmentCost(merchant, account, order,
                    Optional.ofNullable(params.getRevenueRegard())
                            .orElse(AdjustmentRevenueRegardEnum.NONE));
        } finally {
            accountService.releaseAccountLock(params.getAccountId(), params.getRequestId());
        }
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

    private TransactionBizVo buildReimburseData(final Account account,
                                                final AdjustmentReimburseVo order) {
        return TransactionBizVo.builder()
                .requestId(order.getRequestId())
                .businessType(BusinessTypeEnum.ADJUSTMENT)
                .operateSource(OperateSourceEnum.ONLINE)
                .operateMode(OperateModeEnum.MANUAL)
                .transactionAmount(order.getTransactionAmount())
                .transactionCurrency(account.getCurrency())
                .amountPon(AmountPonEnum.POSITIVE)
                .exchangeRate(BigDecimal.ONE)
                .settlementAmount(order.getTransactionAmount())
                .settlementCurrency(account.getCurrency())
                .feeAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .transactionTime(order.getTransactionTime())
                .settlementTime(order.getSettlementTime())
                .comments(order.getComments())
                .remark("Account Adjustment Reimburse")
                .build();
    }

    private TransactionBizVo buildDeductionData(final Account account,
                                                final AdjustmentDeductionVo order) {
        return TransactionBizVo.builder()
                .requestId(order.getRequestId())
                .businessType(BusinessTypeEnum.ADJUSTMENT)
                .operateSource(OperateSourceEnum.ONLINE)
                .operateMode(OperateModeEnum.MANUAL)
                .transactionAmount(order.getTransactionAmount())
                .transactionCurrency(account.getCurrency())
                .amountPon(AmountPonEnum.NEGATIVE)
                .exchangeRate(BigDecimal.ONE)
                .settlementAmount(order.getTransactionAmount())
                .settlementCurrency(account.getCurrency())
                .feeAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .transactionTime(order.getTransactionTime())
                .settlementTime(order.getSettlementTime())
                .comments(order.getComments())
                .remark("Account Adjustment Deduction")
                .build();
    }
}
