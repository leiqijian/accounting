package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.VendorCodeEnum;
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
import com.liquido.statement.pojo.vo.OfflineExchangeVo;
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
public class BizExchangeManager {
    private final BaseService baseService;
    private final RedisDistLock redisDistLock;
    private final AccountService accountService;
    private final TransactionBizService transactionBizService;
    private final TransactionCostService transactionCostService;
    private final AccountStatementService accountStatementService;
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

            // total transferOutAmt = settlementAmount
            final BigDecimal settleAmount = order.getSettlementAmount();
            final BigDecimal accountBalance = account.getLatestDailyBalance()
                    .add(account.getSubTotalAmount());

            /* Step3: check insufficient extractable balance */
            if (Objects.isNull(account.getExchangeAmount())
                    || account.getExchangeAmount().compareTo(settleAmount) < 0) {
                log.error("Insufficient exchangeAmount, accountId={}, "
                                + "exchangeAmount={}, settleAmount={}",
                        account.getId(), account.getExchangeAmount(), settleAmount);
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
                    this.buildAccountStatementBiz(account, transferOut));

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

    @Transactional(rollbackFor = Throwable.class)
    public void offlineExchange(final OfflineExchangeVo vo) {
        /* Step1: data pre-check repeat request (prevent duplicate submissions) */
        this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                vo.getRequestId(), 1, TimeUnit.DAYS);

        /* Step2: get account info */
        final Account account = accountService.getById(vo.getAccountId());
        final MerchantDto merchantInfo = baseService.getMerchantById(account.getMerchantId());

        /* Step3: build biz transaction data */
        final TransactionBizVo order = this.buildTransactionBizData(vo);

        /* Step4: save biz offline exchange order */
        final TransactionBiz bizTransaction =
                transactionBizService.saveTransactionBizOrder(account, order);

        /* Step5: save biz offline exchange cost */
        transactionCostService.saveBizTransactionCost(order, bizTransaction, merchantInfo, account);
    }

    private TransactionBizVo buildTransactionBizData(final OfflineExchangeVo vo) {
        final TransactionBizVo order = new TransactionBizVo();
        order.setRequestId(vo.getRequestId());
        order.setAccountId(vo.getAccountId());
        order.setBusinessType(BusinessTypeEnum.EXCHANGE);
        order.setOperateSource(OperateSourceEnum.OFFLINE);
        order.setOperateMode(OperateModeEnum.MANUAL);
        order.setPaymentChannel(vo.getPaymentChannel());
        order.setSettlementStatus(SettleStatusEnum.SUCCESS);
        order.setAmountPon(AmountPonEnum.NEGATIVE);
        order.setTransactionAmount(vo.getTransactionAmount());
        order.setTransactionAmountUsd(vo.getTransactionAmountUsd());
        order.setTransactionCurrency(vo.getTransactionCurrency());
        order.setExchangeRate(vo.getExchangeRate());
        order.setExchangeLose(vo.getExchangeLose());
        order.setExchangeRateUsd(vo.getExchangeRateUsd());

        order.setSettlementAmount(vo.getSettlementAmount());
        order.setSettlementAmountUsd(vo.getSettlementAmountUsd());
        order.setSettlementCurrency(vo.getSettlementCurrency());

        order.setFeeAmount(Optional.ofNullable(vo.getFeeAmount()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));

        order.setTaxAmount(Optional.ofNullable(vo.getTaxAmount()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));

        order.setFeeAmountUsd(Optional.ofNullable(vo.getFeeAmountUsd()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));

        order.setTaxAmountUsd(Optional.ofNullable(vo.getTaxAmountUsd()).orElse(BigDecimal.ZERO)
                .multiply(AmountPonEnum.NEGATIVE.getCode()));

        order.setComments(StringUtils.defaultIfBlank(vo.getComments(), "exchange offline"));
        order.setRemark(StringUtils.defaultIfBlank(vo.getComments(), "exchange offline"));

        final TransactionBizVo.BizIncomeInfo incomeInfo = Optional.ofNullable(vo.getIncomeInfo())
                .map(v -> TransactionBizVo.BizIncomeInfo.builder()
                        .incomeAmount(vo.getIncomeInfo().getIncomeAmount())
                        .incomeAmountUsd(vo.getIncomeInfo().getIncomeAmountUsd())
                        .build())
                .orElse(TransactionBizVo.BizIncomeInfo.builder()
                        .incomeAmount(BigDecimal.ZERO)
                        .incomeAmountUsd(BigDecimal.ZERO)
                        .build());

        final TransactionBizVo.BizCostInfo costInfo = Optional.ofNullable(vo.getCostInfo())
                .map(v -> TransactionBizVo.BizCostInfo.builder()
                        .vendor(vo.getCostInfo().getVendor())
                        .costFee(vo.getCostInfo().getCostFee())
                        .costTax(vo.getCostInfo().getCostTax())
                        .costFx(vo.getCostInfo().getCostFx())
                        .costOther(vo.getCostInfo().getCostOther())
                        .build())
                .orElse(TransactionBizVo.BizCostInfo.builder()
                        .vendor(VendorCodeEnum.UNKNOWN)
                        .costFee(BigDecimal.ZERO)
                        .costTax(BigDecimal.ZERO)
                        .costFx(BigDecimal.ZERO)
                        .costOther(BigDecimal.ZERO)
                        .build());

        order.setIncomeInfo(incomeInfo);
        order.setCostInfo(costInfo);
        return order;
    }
}

