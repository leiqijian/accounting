package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.Constant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyTransactionBizBo;
import com.liquido.statement.pojo.bo.FxRateInitKey;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.QTransactionBiz;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.QueryLatestExchangeRateVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.statement.repository.TransactionBizRepository;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.DailyExchangeRateService;
import com.liquido.statement.service.TransactionBizService;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionBizServiceImpl implements TransactionBizService {
    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final AccountDailyInitService accountDailyInitService;
    private final TransactionBizRepository transactionBizRepository;
    private final DailyExchangeRateService dailyExchangeRateService;

    @Override
    public TransactionBiz findByTransactionId(final Long transactionId) {
        return transactionBizRepository.findByTransactionId(transactionId);
    }

    @Override
    public TransactionBiz saveTransactionBizOrder(final Account account,
                                                  final TransactionBizVo vo) {

        final LocalDateTime timeNow = LocalDateTimeUtil.nowUtc();
        // initialize transactionTime, settlementTime if null
        vo.setTransactionTime(Optional.ofNullable(vo.getTransactionTime()).orElse(timeNow));
        vo.setSettlementTime(Optional.ofNullable(vo.getSettlementTime()).orElse(timeNow));

        /* SettleTime is UTC+0 time, need to transfer to merchant-account time zone; */
        final LocalDateTime accountZoneTime = vo.getTransactionTime()
                .atZone(Constant.COMMON.ZONE_UTC)
                .withZoneSameInstant(ZoneId.of(account.getTimezone()))
                .toLocalDateTime();

        final AccountDailyInitBo billInitInfo =
                accountDailyInitService.getDailyBillInitInfo(account.getId(),
                        accountZoneTime.toLocalDate());
        if (Objects.isNull(billInitInfo)) {
            log.error("save transaction-biz order fail, daily bill info uninitialized. "
                            + "accountId={}, billDate={}", account.getId(),
                    accountZoneTime.toLocalDate());
            throw StatementExceptionCode.TRANSACTION_SETTLEMENT_FAIL.exception(JsonUtil.toJson(vo));
        }

        final TransactionBiz order = this.buildTransactionBiz(account, vo, billInitInfo);

        return transactionBizRepository.saveAndFlush(order);
    }

    @Override
    public TransactionBiz buildTransactionBiz(final Account account,
                                              final TransactionBizVo vo,
                                              final AccountDailyInitBo billInitInfo) {

        // Get or generate global unique transactionId
        final Long transactionId = Optional.ofNullable(vo.getTransactionId()).orElse(0L) <= 0L
                ? SnowflakeIdUtil.generate() : vo.getTransactionId();

        this.exchangeAmountToUsd(vo, account, vo.getTransactionTime());

        final TransactionBiz order = new TransactionBiz();
        order.setId(SnowflakeIdUtil.generate());
        order.setRequestId(vo.getRequestId());
        order.setTransactionId(transactionId);
        order.setReferenceId(StringUtils.defaultIfBlank(vo.getReferenceId(), ""));

        order.setMerchantId(account.getMerchantId());
        order.setAccountId(account.getId());
        order.setSubMerchantId(vo.getSubMerchantId());
        order.setBillId(billInitInfo.getBillId());
        order.setPaymentConfigId(Optional.ofNullable(vo.getPaymentConfigId()).orElse(0L));

        order.setTransactionType(account.getTransactionTypeCode());
        order.setBusinessType(vo.getBusinessType());
        order.setPaymentChannel(Optional.ofNullable(vo.getPaymentChannel())
                .orElse(PaymentChannelEnum.getDefaultChannel(account.getCountryCode())));
        order.setOperateSource(Optional.ofNullable(vo.getOperateSource())
                .orElse(OperateSourceEnum.ONLINE));
        order.setOperateMode(Optional.ofNullable(vo.getOperateMode())
                .orElse(OperateModeEnum.AUTO));
        order.setTransactionAmount(vo.getTransactionAmount());
        order.setTransactionAmountUsd(
                Optional.ofNullable(vo.getTransactionAmountUsd()).orElse(BigDecimal.ZERO));
        order.setTransactionCurrency(vo.getTransactionCurrency());
        order.setAmountPon(vo.getAmountPon());

        order.setExchangeRate(Optional.ofNullable(vo.getExchangeRate()).orElse(BigDecimal.ONE));
        order.setExchangeLose(Optional.ofNullable(vo.getExchangeLose()).orElse(BigDecimal.ZERO));
        order.setExchangeRateUsd(
                Optional.ofNullable(vo.getExchangeRateUsd()).orElse(BigDecimal.ZERO));

        order.setSettlementAmount(vo.getSettlementAmount());
        order.setSettlementAmountUsd(
                Optional.ofNullable(vo.getSettlementAmountUsd()).orElse(BigDecimal.ZERO));
        order.setSettlementCurrency(vo.getSettlementCurrency());

        order.setFeeAmount(Optional.ofNullable(vo.getFeeAmount()).orElse(BigDecimal.ZERO));
        order.setFeeAmountUsd(Optional.ofNullable(vo.getFeeAmountUsd()).orElse(BigDecimal.ZERO));

        order.setTaxAmount(Optional.ofNullable(vo.getTaxAmount()).orElse(BigDecimal.ZERO));
        order.setTaxAmountUsd(Optional.ofNullable(vo.getTaxAmountUsd()).orElse(BigDecimal.ZERO));

        order.setSettlementStatus(SettleStatusEnum.SUCCESS);
        order.setTransactionTime(vo.getTransactionTime());
        order.setSettlementTime(vo.getSettlementTime());
        order.setExtendInfo(vo.getExtendInfo());

        order.setCreatedBy(Optional.ofNullable(vo.getCreatedBy()).orElse(0L));
        order.setUpdatedBy(Optional.ofNullable(vo.getCreatedBy()).orElse(0L));
        order.setCreatedTime(LocalDateTimeUtil.nowUtc());
        order.setUpdatedTime(LocalDateTimeUtil.nowUtc());
        order.setComments(StringUtils.defaultIfBlank(vo.getComments(), ""));
        order.setRemark(StringUtils.defaultIfBlank(vo.getRemark(),
                StringUtils.defaultIfBlank(vo.getComments(), "")));
        order.setVersion(1);
        return order;
    }

    @Override
    public List<TransactionBiz> saveBatchTransactionBiz(final List<TransactionBiz> list) {
        return transactionBizRepository.saveAllAndFlush(list);
    }

    private void exchangeAmountToUsd(final TransactionBizVo vo,
                                     final Account account,
                                     final LocalDateTime settlementTime) {
        // if businessType is EXCHANGE, No exchange rate conversion required
        if (BusinessTypeEnum.EXCHANGE == vo.getBusinessType()) {
            return;
        }

        // if businessType is topup and topup currency =USD and account currency !=USD
        if (BusinessTypeEnum.TOPUP == vo.getBusinessType()
                && Objects.nonNull(vo.getExchangeRateUsd())
                && vo.getExchangeRateUsd().compareTo(BigDecimal.ZERO) > 0) {
            return;
        }

        final TransactionBizVo.BizCostInfo costInfo =
                Optional.ofNullable(vo.getCostInfo()).orElseGet(
                        () -> TransactionBizVo.BizCostInfo.builder().vendor(VendorCodeEnum.UNKNOWN)
                                .costFee(BigDecimal.ZERO)
                                .costTax(BigDecimal.ZERO)
                                .costFx(BigDecimal.ZERO)
                                .costOther(BigDecimal.ZERO).build());
        vo.setCostInfo(costInfo);

        if (CurrencyEnum.USD == vo.getTransactionCurrency()) {
            vo.setTransactionAmountUsd(vo.getTransactionAmount());
            vo.setSettlementAmountUsd(vo.getSettlementAmountUsd());
            vo.setFeeAmountUsd(vo.getFeeAmount());
            vo.setTaxAmountUsd(vo.getTaxAmount());
            vo.setExchangeRate(BigDecimal.ONE);
            vo.setExchangeLose(BigDecimal.ZERO);
            vo.setExchangeRateUsd(BigDecimal.ONE);
            return;
        }

        DailyExchangeRateDto fxConfig = null;
        try {
            // try load exchange rate by exchange-time
            fxConfig = dailyExchangeRateService.queryDailyExchangeRate(FxRateInitKey.builder()
                    .merchantId(account.getMerchantId())
                    .accountId(account.getId())
                    .exchangeTime(settlementTime.withMinute(0).withSecond(0).withNano(0))
                    .sourceCurrency(CurrencyEnum.USD)
                    .targetCurrency(vo.getTransactionCurrency())
                    .build());
            // if null reload real-time exchange rate
            if (Objects.isNull(fxConfig)) {
                fxConfig = modelMapper.convertBo(dailyExchangeRateService.queryLatestExchangeRate(
                        QueryLatestExchangeRateVo.builder()
                                .merchantId(account.getMerchantId())
                                .accountId(account.getId())
                                .sourceCurrency(CurrencyEnum.USD)
                                .targetCurrency(vo.getTransactionCurrency())
                                .build()));
            }
        } catch (Exception e) {
            log.warn("get exchange rate fail");
        }

        if (Objects.isNull(fxConfig)) {
            throw StatementExceptionCode.GET_EXCHANGE_RATE_FAIL.exception();
        }

        vo.setTransactionAmountUsd(AmountUtil.division(vo.getTransactionAmount(),
                fxConfig.getMerchantRate(), 6, RoundingMode.HALF_UP));
        vo.setSettlementAmountUsd(AmountUtil.division(vo.getSettlementAmount(),
                fxConfig.getMerchantRate(), 6, RoundingMode.HALF_UP));

        vo.setFeeAmountUsd(AmountUtil.division(vo.getFeeAmount(),
                fxConfig.getMerchantRate(), 6, RoundingMode.HALF_UP));
        vo.setTaxAmountUsd(AmountUtil.division(vo.getTaxAmount(),
                fxConfig.getMerchantRate(), 6, RoundingMode.HALF_UP));

        vo.setExchangeRateUsd(fxConfig.getMerchantRate());
        vo.setExchangeRate(BigDecimal.ONE);
        vo.setExchangeLose(BigDecimal.ZERO);

        if (account.getCurrency() != vo.getTransactionCurrency()) {
            vo.setExchangeRate(fxConfig.getMerchantRate());
            vo.setExchangeLose(fxConfig.getRatioLose());
        }
    }

    @Override
    public TransactionBiz updateTransactionBizOrder(TransactionBiz order) {
        return transactionBizRepository.saveAndFlush(order);
    }

    @Override
    public void updateTransactionBizStatus(final Long transactionId,
                                           final SettleStatusEnum fromStatus,
                                           final SettleStatusEnum toStatus) {

        final QTransactionBiz entity = QTransactionBiz.transactionBiz;
        final long result = jpaQueryFactory.update(entity)
                .set(entity.settlementStatus, toStatus)
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.transactionId.eq(transactionId)
                        .and(entity.settlementStatus.eq(fromStatus)))
                .execute();

        if (result <= 0) {
            throw StatementExceptionCode.PAYMENT_PAYOUT_FAIL.exception();
        }
    }

    @Override
    public List<DailyTransactionBizBo> statisticsDailyBill(
            final Account account,
            final AccountDailyInitBo dailyInit) {

        final QTransactionBiz entity = QTransactionBiz.transactionBiz;
        final BooleanExpression condition = entity.accountId.eq(account.getId())
                .and(entity.billId.eq(dailyInit.getBillId()))
                .and(entity.settlementStatus.eq(SettleStatusEnum.SUCCESS))
                .and(entity.operateSource.eq(OperateSourceEnum.ONLINE));

        final NumberTemplate<BigDecimal> settlementAmount =
                Expressions.numberTemplate(BigDecimal.class, " ({0} * {1}) ",
                        entity.settlementAmount, entity.amountPon);

        final QBean<DailyTransactionBizBo> bean = Projections.fields(DailyTransactionBizBo.class,
                entity.businessType.as("businessType"),
                settlementAmount.sum().coalesce(BigDecimal.ZERO).as("totalAmount"),
                entity.feeAmount.sum().coalesce(BigDecimal.ZERO).as("totalFee"),
                entity.taxAmount.sum().coalesce(BigDecimal.ZERO).as("totalTax"),
                entity.id.count().coalesce(0L).as("totalCount"));

        final List<DailyTransactionBizBo> dataList = jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .groupBy(entity.businessType)
                .fetch();

        // set default value if empty
        for (final BusinessTypeEnum type : BusinessTypeEnum.values()) {
            if (dataList.stream().noneMatch(item -> item.getBusinessType() == type)) {
                dataList.add(DailyTransactionBizBo.builder()
                        .businessType(type)
                        .totalAmount(BigDecimal.ZERO)
                        .totalFee(BigDecimal.ZERO)
                        .totalTax(BigDecimal.ZERO)
                        .totalCount(0L)
                        .build());
            }
        }

        return dataList;
    }

    @Override
    public List<TransactionBiz> queryPaymentInProgressRecord() {
        final QTransactionBiz entity = QTransactionBiz.transactionBiz;
        final BooleanExpression condition = entity.businessType.eq(BusinessTypeEnum.TRANSFER_OUT)
                .and(entity.settlementStatus.eq(SettleStatusEnum.PROCESSING))
                .and(entity.paymentConfigId.gt(0L));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetch();
    }
}
