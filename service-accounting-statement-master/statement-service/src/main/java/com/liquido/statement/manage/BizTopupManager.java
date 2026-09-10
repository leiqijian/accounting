package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisDistLock;
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
public class BizTopupManager {
    private final RedisDistLock redisDistLock;
    private final AccountService accountService;
    private final TransactionBizService transactionBizService;
    private final AccountStatementService accountStatementService;
    private final AccountStatementBizService accountStatementBizService;
    private final TransactionCostService transactionCostService;
    private final BaseService baseService;

    /**
     * Account topUp with account lock
     */
    @Transactional(rollbackFor = Throwable.class)
    public void accountTopUp(final TransactionBizVo order) {
        try {
            /* data pre-check repeat request (prevent duplicate submissions) */
            this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                    order.getRequestId(), 1, TimeUnit.DAYS);

            final Account account = accountService.getAccountLocked(
                    order.getAccountId(),
                    order.getRequestId());

            if (account.getCurrency() != order.getSettlementCurrency()) {
                throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
            }

            /* execute topup */
            this.executeTopUp(order, account);
        } finally {
            accountService.releaseAccountLock(order.getAccountId(), order.getRequestId());
        }
    }

    /**
     * Account topUp with account non-lock
     */
    @Transactional(rollbackFor = Throwable.class)
    public void executeTopUp(final TransactionBizVo order, final Account account) {
        order.setAmountPon(AmountPonEnum.POSITIVE);
        order.setBusinessType(BusinessTypeEnum.TOPUP);
        order.setFeeAmount(Optional.ofNullable(order.getFeeAmount()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));
        order.setTaxAmount(Optional.ofNullable(order.getTaxAmount()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));

        /* Step1: save order */
        final TransactionBiz topup = transactionBizService.saveTransactionBizOrder(account, order);
        final BigDecimal settleAmount = topup.getSettlementAmount()
                .multiply(topup.getAmountPon().getCode())
                // is negative value
                .add(topup.getFeeAmount())
                // is negative value
                .add(topup.getTaxAmount());

        /* Step2: increase account balance */
        final BigDecimal startBalance =
                account.getLatestDailyBalance().add(account.getSubTotalAmount());
        final BigDecimal endBalance = startBalance.add(settleAmount);
        if (!accountService.increaseAccountBalance(account, settleAmount, settleAmount, 1)) {
            throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
        }

        accountStatementBizService.saveAccountStatementBiz(
                this.buildAccountStatementBiz(account, topup, settleAmount));

        /* Step3: save account statement flow*/
        accountStatementService.saveTransactionBizAccountFlow(
                topup, settleAmount, startBalance, endBalance);

        final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());
        /* Step4: save biz transaction cost*/
        transactionCostService.saveBizTransactionCost(order, topup, merchant, account);

    }

    private AccountStatementBizBo buildAccountStatementBiz(final Account account,
                                                           final TransactionBiz biz,
                                                           final BigDecimal settleAmount) {
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
                .extractableAmount(settleAmount)
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
