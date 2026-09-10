package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ExtraFeeGroupEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.base.pojo.dto.ApmCostConfigDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.vo.ApmCostConfigVo;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.enums.AdjustmentRevenueRegardEnum;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.CardCostConfigBo;
import com.liquido.statement.pojo.bo.FxRateInitKey;
import com.liquido.statement.pojo.bo.RecalculateCostBo;
import com.liquido.statement.pojo.bo.RecalculateCostData;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.QTransactionCost;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.entity.TransactionCost;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.vo.ExtendData;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.statement.pojo.vo.TransactionCostVo;
import com.liquido.statement.pojo.vo.TransactionExtraFeeVo;
import com.liquido.statement.pojo.vo.TransactionFeeVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.repository.TransactionCostRepository;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.DailyExchangeRateService;
import com.liquido.statement.service.TransactionCostService;
import com.liquido.statement.service.TransactionFeeService;
import com.liquido.statement.service.TransactionMoneyService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionCostServiceImpl implements TransactionCostService {

    private final BaseService baseService;
    private final AccountService accountService;
    private final JPAQueryFactory jpaQueryFactory;
    private final TransactionCostRepository repository;
    private final TransactionFeeService transactionFeeService;
    private final TransactionMoneyService transactionMoneyService;
    private final DailyExchangeRateService dailyExchangeRateService;

    private static final List<DirectionTypeEnum> REFUND_CHARGE_BACK_TYPES =
            List.of(DirectionTypeEnum.REFUND,
                    DirectionTypeEnum.CHARGE_BACK);

    @Override
    public List<RecalculateCostData> batchLoadTransactionCost(final RecalculateCostBo vo) {
        final QTransactionCost entity = QTransactionCost.transactionCost;
        BooleanExpression condition = entity.accountId.eq(vo.getAccountId())
                .and(entity.billId.eq(vo.getBillId()))
                .and(entity.id.gt(Optional.ofNullable(vo.getCostId()).orElse(0L) <= 0L ? 0L
                        : vo.getCostId()))
                .and(entity.businessType.eq(BusinessTypeEnum.TRANSACTION))
                .and(entity.vendor.ne(VendorCodeEnum.UNKNOWN));

        final QBean<RecalculateCostData> bean = Projections.fields(RecalculateCostData.class,
                entity.id,
                entity.uniqueId,
                entity.transactionId,
                entity.accountId,
                entity.countryCode,
                entity.transactionTypeCode,
                entity.directionType,
                entity.productCode,
                entity.vendor,
                entity.transactionTime,
                entity.transactionTimestamp,
                entity.fxRate,
                entity.fxLose,
                entity.amount,
                entity.currency,
                entity.amountUsd,
                entity.feeUsd,
                entity.taxUsd,
                entity.fxUsd);

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .orderBy(entity.id.asc())
                .limit(vo.getBathSize())
                .fetch();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public TransactionCost queryTransactionCostInfo(
            final Long accountId,
            final Long transactionId,
            final DirectionTypeEnum directionType) {

        final QTransactionCost entity = QTransactionCost.transactionCost;
        final BooleanExpression condition = entity.accountId.eq(accountId)
                .and(entity.transactionId.eq(transactionId))
                .and(entity.directionType.eq(directionType));

        return jpaQueryFactory.select(entity).from(entity)
                .where(condition)
                .orderBy(entity.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveTransactionCost(final TransactionCost cost) {
        repository.saveAndFlush(cost);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchUpdateTransactionCost(final List<RecalculateCostData> costList) {
        for (final RecalculateCostData cost : costList) {
            final QTransactionCost entity = QTransactionCost.transactionCost;
            jpaQueryFactory.update(entity)
                    .set(entity.taxUsd, cost.getTaxUsd())
                    .set(entity.extraFeeUsd, cost.getExtraFeeUsd())
                    .set(entity.extraTaxUsd, cost.getExtraTaxUsd())
                    .set(entity.extraFxUsd, cost.getExtraFxUsd())
                    .set(entity.fxUsd, cost.getFxUsd())
                    .set(entity.fxLose, cost.getFxLose())
                    .set(entity.costFee, cost.getCostFee())
                    .set(entity.costTax, cost.getCostTax())
                    .set(entity.costFx, cost.getCostFx())
                    .set(entity.costOther, cost.getCostOther())
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(cost.getId()))
                    .execute();
        }
    }

    @Override
    @Async("reRunTaskExecutor")
    public CompletableFuture<Boolean> processTransactionCostData(
            final List<TransactionMoney> orderList) {

        final Set<Long> transactionIds = orderList.stream().map(TransactionMoney::getTransactionId)
                .collect(Collectors.toSet());
        final Set<Long> merchantIds =
                orderList.stream().map(TransactionMoney::getMerchantId).collect(Collectors.toSet());
        final Set<Long> accountIds =
                orderList.stream().map(TransactionMoney::getAccountId).collect(Collectors.toSet());

        // load merchant info
        final Map<Long, MerchantDto> merchantMap = Maps.newHashMap();
        for (final Long merchantId : merchantIds) {
            merchantMap.put(merchantId, baseService.getMerchantById(merchantId));
        }

        // load account info
        final Map<Long, Account> accountMap = accountService.findByIds(accountIds).stream()
                .collect(Collectors.toMap(Account::getId, Function.identity(), (k1, k2) -> k2));

        // load transaction fee
        final Map<String, List<TransactionFee>> feeMaps =
                transactionFeeService.findTransactionFee(transactionIds).stream()
                        .collect(Collectors.groupingBy(item -> item.getTransactionId()
                                + item.getDirectionType().getCode()));

        final List<TransactionCost> resultList = orderList.stream().map(order -> {
            // build transaction cost entity
            final TransactionCost transactionCost =
                    this.wrapTransactionCost(order, merchantMap.get(order.getMerchantId()),
                            accountMap.get(order.getAccountId()));

            // calculation fee tax
            this.calculationFeeUsdAndTaxUsd(transactionCost,
                    feeMaps.get(order.getTransactionId() + order.getDirectionType().getCode()),
                    accountMap.get(order.getAccountId()));

            return transactionCost;
        }).collect(Collectors.toList());

        repository.saveAllAndFlush(resultList);
        resultList.clear();

        return CompletableFuture.completedFuture(Boolean.TRUE);
    }

    private TransactionCost wrapTransactionCost(final TransactionMoney order,
                                                final MerchantDto merchant,
                                                final Account account) {

        // When Order.directionType == REFUND then extra_xxx=0, fx_usd.negate, cost_fx.negate
        BigDecimal fxUsd = (order.getSettlementAmountUsd()
                .multiply(order.getFxLoseUsd()))
                .multiply(BusinessStrategyEnum.parse(order.getTransactionTypeCode(),
                        order.getDirectionType()).getFeePon().getCode());
        if (DirectionTypeEnum.REFUND == order.getDirectionType()) {
            fxUsd = fxUsd.negate();
        }

        final TransactionCost cost = new TransactionCost();
        cost.setId(order.getId());
        cost.setUniqueId(order.getUniqueId());
        cost.setTransactionId(order.getTransactionId());
        cost.setMerchantId(order.getMerchantId());
        cost.setAccountId(order.getAccountId());
        cost.setBillId(order.getBillId());
        cost.setCountryCode(account.getCountryCode());
        cost.setMerchantCode(merchant.getCode());
        cost.setBusinessType(BusinessTypeEnum.TRANSACTION);
        cost.setTransactionTypeCode(order.getTransactionTypeCode());
        cost.setDirectionType(order.getDirectionType());
        cost.setProductCode(order.getProductCode());
        cost.setVendor(order.getVendor());
        cost.setOperateSource(OperateSourceEnum.ONLINE);
        cost.setAmount(order.getAmount().multiply(order.getAmountPon()));
        cost.setCurrency(order.getCurrency());
        cost.setAmountUsd(order.getSettlementAmountUsd().multiply(order.getAmountPon()));
        cost.setFeeUsd(BigDecimal.ZERO);
        cost.setTaxUsd(BigDecimal.ZERO);
        cost.setFxRate(order.getFxRateUsd());
        cost.setFxLose(order.getFxLoseUsd());
        cost.setFxUsd(fxUsd);
        cost.setExtraFeeUsd(BigDecimal.ZERO);
        cost.setExtraTaxUsd(BigDecimal.ZERO);
        cost.setExtraFxUsd(BigDecimal.ZERO);
        cost.setCostFee(BigDecimal.ZERO);
        cost.setCostTax(BigDecimal.ZERO);
        cost.setCostFx(BigDecimal.ZERO);
        cost.setCostOther(BigDecimal.ZERO);
        cost.setTransactionTime(order.getTransactionTime());
        cost.setTransactionTimestamp(LocalDateTimeUtil.utcToInstant(order.getTransactionTime()));
        cost.setCreatedTime(order.getCreatedTime());
        cost.setUpdatedTime(order.getUpdatedTime());
        cost.setVersion(1);
        cost.setDelFlag(Boolean.FALSE);
        cost.setRemark("SYNC_COST_DATA");
        return cost;

    }

    private void calculationFeeUsdAndTaxUsd(
            final TransactionCost transactionCost,
            final List<TransactionFee> transactionFees,
            final Account account) {

        BigDecimal totalFeeUsd = BigDecimal.ZERO;
        BigDecimal totalTaxUsd = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(transactionFees)) {
            transactionCost.setFeeUsd(totalFeeUsd);
            transactionCost.setTaxUsd(totalTaxUsd);
            return;
        }

        for (final TransactionFee fee : transactionFees) {
            BigDecimal usdAmount = fee.getSettlementAmountUsd();
            if (usdAmount.compareTo(BigDecimal.ZERO) == 0) {
                usdAmount = fee.getSettlementAmount();
                if (CurrencyEnum.USD != fee.getSettlementCurrency()) {
                    final LocalDateTime exchangeTime = transactionCost.getTransactionTime()
                            .withMinute(0).withSecond(0).withNano(0);
                    final DailyExchangeRateDto fxConfig =
                            dailyExchangeRateService.queryDailyExchangeRate(
                                    FxRateInitKey.builder()
                                            .merchantId(account.getMerchantId())
                                            .accountId(account.getId())
                                            .exchangeTime(exchangeTime)
                                            .sourceCurrency(CurrencyEnum.USD)
                                            .targetCurrency(fee.getSettlementCurrency())
                                            .build());
                    usdAmount = AmountUtil.division(fee.getSettlementAmount(),
                            fxConfig.getMerchantRate(), 6, RoundingMode.HALF_UP);
                }
            }

            usdAmount = usdAmount.multiply(fee.getAmountPon());

            if (FeeGroupEnum.TAX == fee.getFeeGroup()) {
                totalTaxUsd = totalTaxUsd.add(usdAmount);
            } else {
                totalFeeUsd = totalFeeUsd.add(usdAmount);
            }

        }

        transactionCost.setFeeUsd(totalFeeUsd);
        transactionCost.setTaxUsd(totalTaxUsd);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchSaveTransactionCost(final List<TransactionMoneyVo> orderList,
                                         final AccountDailyInitBo dailyInitBo) {

        final List<TransactionCost> costList = this.wrapTransactionCost(orderList, dailyInitBo);
        if (CollectionUtils.isNotEmpty(costList)) {
            repository.saveAllAndFlush(costList);
        }
    }

    @Override
    public void batchSaveTransactionCost(final List<TransactionCost> costList) {
        if (CollectionUtils.isNotEmpty(costList)) {
            repository.saveAllAndFlush(costList);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveBizTransactionCost(final TransactionBizVo vo, final TransactionBiz order,
                                       final MerchantDto merchantInfo, final Account account) {
        this.saveTransactionCost(this.buildBizTransactionCost(vo, order, merchantInfo, account));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public TransactionCost buildBizTransactionCost(final TransactionBizVo vo,
                                                   final TransactionBiz order,
                                                   final MerchantDto merchantInfo,
                                                   final Account account) {
        final TransactionBizVo.BizIncomeInfo incomeInfo = Optional.ofNullable(vo.getIncomeInfo())
                .map(v -> {
                    v.setIncomeAmount(Optional.ofNullable(v.getIncomeAmount())
                            .orElse(BigDecimal.ZERO).negate());
                    v.setIncomeAmountUsd(Optional.ofNullable(v.getIncomeAmountUsd())
                            .orElse(BigDecimal.ZERO).negate());
                    return v;
                }).orElse(TransactionBizVo.BizIncomeInfo.builder()
                        .incomeAmount(BigDecimal.ZERO)
                        .incomeAmountUsd(BigDecimal.ZERO)
                        .build());

        final TransactionBizVo.BizCostInfo costInfo = Optional.ofNullable(vo.getCostInfo())
                .map(v -> {
                    v.setVendor(Optional.ofNullable(v.getVendor()).orElse(VendorCodeEnum.UNKNOWN));
                    v.setCostFee(Optional.ofNullable(v.getCostFee()).orElse(BigDecimal.ZERO));
                    v.setCostTax(Optional.ofNullable(v.getCostTax()).orElse(BigDecimal.ZERO));
                    v.setCostFx(Optional.ofNullable(v.getCostFx()).orElse(BigDecimal.ZERO));
                    v.setCostOther(Optional.ofNullable(v.getCostOther()).orElse(BigDecimal.ZERO));
                    return v;
                }).orElse(TransactionBizVo.BizCostInfo.builder()
                        .vendor(VendorCodeEnum.UNKNOWN)
                        .costFee(BigDecimal.ZERO)
                        .costTax(BigDecimal.ZERO)
                        .costFx(BigDecimal.ZERO)
                        .costOther(BigDecimal.ZERO)
                        .build());

        return TransactionCost.builder()
                .id(SnowflakeIdUtil.generate())
                .transactionId(order.getTransactionId())
                .uniqueId(order.getRequestId())
                .merchantId(order.getMerchantId())
                .accountId(order.getAccountId())
                .billId(order.getBillId())
                .countryCode(account.getCountryCode())
                .merchantCode(merchantInfo.getCode())
                .businessType(order.getBusinessType())
                .transactionTypeCode(account.getTransactionTypeCode())
                .directionType(DirectionTypeEnum.SETTLED)
                .productCode(ProductCodeEnum.parse(order.getPaymentChannel().getCode()))
                .vendor(costInfo.getVendor())
                .subMerchantId(order.getSubMerchantId())
                .operateSource(order.getOperateSource())
                .currency(order.getTransactionCurrency())
                .amount(BigDecimal.ZERO)
                .amountUsd(BigDecimal.ZERO)

                // fee info
                .feeUsd(order.getFeeAmountUsd())
                .taxUsd(order.getTaxAmountUsd())
                .fxUsd(BigDecimal.ZERO)

                // extra fee info
                .extraFeeUsd(BigDecimal.ZERO)
                .extraTaxUsd(BigDecimal.ZERO)
                .extraFxUsd(incomeInfo.getIncomeAmountUsd())

                // fx info
                .fxRate(order.getExchangeRateUsd())
                .fxLose(order.getExchangeLose())

                // cost info
                .costFee(costInfo.getCostFee())
                .costTax(costInfo.getCostTax())
                .costFx(costInfo.getCostFx())
                .costOther(costInfo.getCostOther())

                .transactionTime(order.getTransactionTime())
                .transactionTimestamp(LocalDateTimeUtil.utcToInstant(order.getTransactionTime()))
                .createdTime(LocalDateTimeUtil.nowUtc()).updatedTime(LocalDateTimeUtil.nowUtc())
                .remark(order.getRemark())
                .version(1).build();

    }

    @Override
    public void saveBizAdjustmentCost(
            final MerchantDto merchantInfo,
            final Account account,
            final TransactionBiz order,
            final AdjustmentRevenueRegardEnum revenueRegard) {

        final BigDecimal revenueAmt = order.getSettlementAmountUsd()
                .multiply(order.getAmountPon().getCode());

        final TransactionCost cost = TransactionCost.builder()
                .id(SnowflakeIdUtil.generate())
                .transactionId(order.getTransactionId())
                .uniqueId(order.getRequestId())
                .merchantId(order.getMerchantId())
                .accountId(order.getAccountId())
                .billId(order.getBillId())
                .countryCode(account.getCountryCode())
                .merchantCode(merchantInfo.getCode())
                .businessType(order.getBusinessType())
                .transactionTypeCode(account.getTransactionTypeCode())
                .directionType(DirectionTypeEnum.SETTLED)
                .productCode(ProductCodeEnum.parse(order.getPaymentChannel().getCode()))
                .vendor(VendorCodeEnum.UNKNOWN)
                .operateSource(order.getOperateSource())
                .currency(order.getTransactionCurrency())
                .amount(BigDecimal.ZERO)
                .amountUsd(BigDecimal.ZERO)

                // fee info
                .feeUsd(BigDecimal.ZERO)
                .taxUsd(BigDecimal.ZERO)
                .fxUsd(BigDecimal.ZERO)

                // extra fee info
                .extraFeeUsd(AdjustmentRevenueRegardEnum.FEE == revenueRegard
                        ? revenueAmt : BigDecimal.ZERO)
                .extraTaxUsd(AdjustmentRevenueRegardEnum.TAX == revenueRegard
                        ? revenueAmt : BigDecimal.ZERO)
                .extraFxUsd(AdjustmentRevenueRegardEnum.FX == revenueRegard
                        ? revenueAmt : BigDecimal.ZERO)

                // fx info
                .fxRate(order.getExchangeRateUsd())
                .fxLose(order.getExchangeLose())

                // cost info
                .costFee(BigDecimal.ZERO)
                .costTax(BigDecimal.ZERO)
                .costFx(BigDecimal.ZERO)
                .costOther(BigDecimal.ZERO)

                .transactionTime(order.getTransactionTime())
                .transactionTimestamp(LocalDateTimeUtil.utcToInstant(order.getTransactionTime()))
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .remark(order.getRemark())
                .version(1).build();

        this.saveTransactionCost(cost);
    }

    private List<TransactionCost> wrapTransactionCost(
            final List<TransactionMoneyVo> orderList,
            final AccountDailyInitBo dailyInitBo) {

        if (CollectionUtils.isEmpty(orderList)) {
            return Collections.emptyList();
        }

        final List<TransactionCost> costList = new ArrayList<>(orderList.size());
        for (final TransactionMoneyVo order : orderList) {
            // When order is payout:rejected or
            // payin:charge_back_rejected then regardless of cost
            if (List.of(DirectionTypeEnum.REJECTED, DirectionTypeEnum.CHARGE_BACK_REJECTED)
                    .contains(order.getDirectionType())) {
                continue;
            }

            // transaction cost fee
            BigDecimal costFee = BigDecimal.ZERO;
            // tax cost
            BigDecimal costTax = BigDecimal.ZERO;
            // fx cost
            BigDecimal costFx = BigDecimal.ZERO;
            // other cost
            BigDecimal costOther = BigDecimal.ZERO;

            // transaction revenue fee
            BigDecimal feeUsd = BigDecimal.ZERO;
            BigDecimal taxUsd = BigDecimal.ZERO;
            BigDecimal fxLoseUsd =
                    Optional.ofNullable(order.getFxUsdLose()).orElse(BigDecimal.ZERO);
            BigDecimal fxUsd = (order.getSettlementAmountUsd().multiply(fxLoseUsd))
                    .multiply(order.getBusinessStrategy().getFeePon().getCode());

            // extra transaction revenue fee
            BigDecimal extraFeeUsd = BigDecimal.ZERO;
            BigDecimal extraTaxUsd = BigDecimal.ZERO;
            BigDecimal extraFxUsd = BigDecimal.ZERO;

            BigDecimal configFxUsd = BigDecimal.ZERO;
            BigDecimal configFxLose = BigDecimal.ZERO;

            boolean fxUsdUseConfig = false;
            boolean fxLoseUseConfig = false;

            if (CollectionUtils.isNotEmpty(order.getTransactionCostList())) {
                for (final TransactionCostVo vo : order.getTransactionCostList()) {
                    final BigDecimal volume =
                            Optional.ofNullable(vo.getVolume()).orElse(BigDecimal.ZERO);
                    if (FeeGroupEnum.TRANSACTION_FEE == vo.getFeeGroup()) {
                        costFee = costFee.add(volume);
                    } else if (FeeGroupEnum.TAX == vo.getFeeGroup()) {
                        costTax = costTax.add(volume);
                    } else if (FeeGroupEnum.FX == vo.getFeeGroup()) {
                        costFx = costFx.add(volume);
                    } else {
                        costOther = costOther.add(volume);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(order.getTransactionExtraFeeList())) {
                for (final TransactionExtraFeeVo vo : order.getTransactionExtraFeeList()) {
                    final BigDecimal volume = vo.getVolume();

                    if (ExtraFeeGroupEnum.FX == vo.getExtraFeeGroup()) {
                        fxUsdUseConfig = true;
                        configFxUsd = configFxUsd.add(volume);
                    } else if (ExtraFeeGroupEnum.FX_LOSE == vo.getExtraFeeGroup()) {
                        fxLoseUseConfig = true;
                        configFxLose = configFxLose.add(volume);
                    }

                    if (ExtraFeeGroupEnum.EXTRA_FEE == vo.getExtraFeeGroup()) {
                        extraFeeUsd = extraFeeUsd.add(volume);
                    } else if (ExtraFeeGroupEnum.EXTRA_TAX == vo.getExtraFeeGroup()) {
                        extraTaxUsd = extraTaxUsd.add(volume);
                    } else if (ExtraFeeGroupEnum.EXTRA_FX == vo.getExtraFeeGroup()) {
                        extraFxUsd = extraFxUsd.add(volume);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(order.getTransactionFeeList())) {
                for (final TransactionFeeVo feeVo : order.getTransactionFeeList()) {
                    if (Objects.isNull(feeVo.getSettlementAmountUsd())) {
                        continue;
                    }

                    if (FeeGroupEnum.TAX == feeVo.getFeeGroup()) {
                        taxUsd = taxUsd.add(feeVo.getSettlementAmountUsd());
                    } else {
                        feeUsd = feeUsd.add(feeVo.getSettlementAmountUsd());
                    }
                }
            }

            /* override fee/tax/fxUsd、fxLose when use config */
            if (fxUsdUseConfig) {
                fxUsd = configFxUsd.multiply(order.getBusinessStrategy().getFeePon().getCode());
            }
            if (fxLoseUseConfig) {
                fxLoseUsd = configFxLose;
            }

            /* When Order.directionType == REFUND then extra_xxx=0, taxUsd=0, costTax=0
               fx_usd.negate, cost_fx.negate */
            if (DirectionTypeEnum.REFUND == order.getDirectionType()
                    || DirectionTypeEnum.CHARGE_BACK == order.getDirectionType()) {
                extraFeeUsd = BigDecimal.ZERO;
                extraTaxUsd = BigDecimal.ZERO;
                extraFxUsd = BigDecimal.ZERO;
                costTax = BigDecimal.ZERO;

                fxUsd = fxUsd.negate();
                costFx = costFx.negate();
            }

            final TransactionCost cost = TransactionCost.builder()
                    .id(SnowflakeIdUtil.generate())
                    .transactionId(order.getTransactionId())
                    .uniqueId(order.getUniqueId())
                    .merchantId(order.getMerchantId())
                    .subMerchantId(order.getSubMerchantId())
                    .accountId(order.getAccountId())
                    .billId(dailyInitBo.getBillId())
                    .countryCode(order.getCountryCode())
                    .merchantCode(order.getMerchantCode())
                    .businessType(BusinessTypeEnum.TRANSACTION)
                    .transactionTypeCode(order.getTransactionTypeCode())
                    .directionType(order.getDirectionType()).productCode(order.getProductCode())
                    .vendor(order.getVendor())
                    .operateSource(OperateSourceEnum.ONLINE)
                    .amount(order.getAmount()
                            .multiply(order.getBusinessStrategy().getAmountPon().getCode()))
                    .currency(order.getCurrency())
                    .amountUsd(order.getSettlementAmountUsd()
                            .multiply(order.getBusinessStrategy().getAmountPon().getCode()))
                    .feeUsd(feeUsd.multiply(order.getBusinessStrategy().getFeePon().getCode()))
                    .taxUsd(taxUsd.multiply(order.getBusinessStrategy().getFeePon().getCode()))
                    .fxUsd(fxUsd)
                    .extraFeeUsd(
                            extraFeeUsd.multiply(order.getBusinessStrategy().getFeePon().getCode()))
                    .extraTaxUsd(
                            extraTaxUsd.multiply(order.getBusinessStrategy().getFeePon().getCode()))
                    .extraFxUsd(
                            extraFxUsd.multiply(order.getBusinessStrategy().getFeePon().getCode()))
                    .fxRate(order.getFxUsdRate()).fxLose(fxLoseUsd)
                    .costFee(costFee)
                    .costTax(costTax)
                    .costFx(costFx)
                    .costOther(costOther)
                    .transactionTime(order.getTransactionTime()).transactionTimestamp(
                            LocalDateTimeUtil.utcToInstant(order.getTransactionTime()))
                    .createdTime(LocalDateTimeUtil.nowUtc()).updatedTime(LocalDateTimeUtil.nowUtc())
                    .version(1).build();

            costList.add(cost);
        }

        return costList;
    }

    private void excludeShopifyFeeRules(final CardCostConfigurationDto cardCostConfig) {
        if (Objects.nonNull(cardCostConfig)
                && CollectionUtils.isNotEmpty(cardCostConfig.getCardCostConfigList())) {

            final List<CardCostConfigDto> cardCostConfigList =
                    cardCostConfig.getCardCostConfigList().stream()
                            .filter(item -> !StringUtils.equalsIgnoreCase("SHOPIFY_FEE",
                                    item.getFeeName()))
                            .collect(Collectors.toList());
            // exclude shopify fee rules
            cardCostConfig.setCardCostConfigList(cardCostConfigList);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @Async("reRunTaskExecutor")
    public CompletableFuture<Boolean> doRecalculateCost(
            final List<RecalculateCostData> costList,
            final RecalculateCostBo recalculateCostBo,
            final ApmCostConfigurationDto apmCostConfig,
            final CardCostConfigurationDto cardCostConfig,
            final List<ExtraIncomeConfigurationDto> extraIncomeConfig) {

        List<RecalculateCostData> dataList = costList.stream()
                .filter(x -> recalculateCostBo.getProductCodes().contains(x.getProductCode()))
                .collect(Collectors.toList());

        dataList = CollectionUtils.isNotEmpty(dataList) ? dataList : costList;

        // card transaction order list
        final List<RecalculateCostData> cardOrderList = dataList.stream()
                .filter(data -> ProductCodeEnum.CARD == data.getProductCode())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cardOrderList)) {
            this.recalculateCardCost(cardOrderList, cardCostConfig, extraIncomeConfig);
        }

        // apm transaction order list
        final List<RecalculateCostData> apmOrderList = dataList.stream()
                .filter(data -> ProductCodeEnum.CARD != data.getProductCode())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(apmOrderList)) {
            this.recalculateApmCost(apmOrderList, apmCostConfig, extraIncomeConfig);
        }

        return CompletableFuture.completedFuture(Boolean.TRUE);
    }

    private void recalculateApmCost(
            final List<RecalculateCostData> costList,
            final ApmCostConfigurationDto costConfigDto,
            final List<ExtraIncomeConfigurationDto> extraFeeConfig) {

        for (final RecalculateCostData cost : costList) {

            final List<ApmCostConfigDto> costConfigList =
                    baseService.getApmCostConfig(
                            costConfigDto.getCostConfigList(),
                            ApmCostConfigVo.builder()
                                    .accountId(cost.getAccountId())
                                    .country(cost.getCountryCode())
                                    .transactionType(cost.getTransactionTypeCode())
                                    .vendor(cost.getVendor())
                                    .productCode(cost.getProductCode())
                                    .build());

            if (CollectionUtils.isEmpty(costConfigList)) {
                log.warn("cost config not fund, skip recalculate cost ={}", cost);
                cost.setRemark("cost config not fund");
            }

            // Calculate Cost
            final List<TransactionCostVo> costFeeList = Lists.newArrayList();

            final BigDecimal minVolume = costConfigList.stream()
                    .filter(x -> FeeTypeCodeEnum.TRANSACTION_FEE == x.getFeeType())
                    .map(x -> CurrencyEnum.USD == x.getCurrency()
                            ? x.getMinVolume()
                            : AmountUtil.division(x.getMinVolume(), cost.getFxRate(), 6))
                    .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

            final BigDecimal maxVolume = costConfigList.stream()
                    .filter(x -> FeeTypeCodeEnum.TRANSACTION_FEE == x.getFeeType())
                    .map(x -> CurrencyEnum.USD == x.getCurrency()
                            ? x.getMaxVolume()
                            : AmountUtil.division(x.getMaxVolume(), cost.getFxRate(), 6))
                    .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);


            BigDecimal costFee = costConfigList.stream()
                    .filter(x -> FeeGroupEnum.TRANSACTION_FEE == x.getFeeGroup())
                    .filter(x -> !StringUtils.equalsIgnoreCase("SHOPIFY_FEE", x.getFeeName()))
                    .map(x -> calculateApmCostFee(cost, BigDecimal.ZERO, x))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (minVolume.compareTo(BigDecimal.ZERO) > 0) {
                costFee = costFee.max(minVolume);
            }
            if (maxVolume.compareTo(BigDecimal.ZERO) > 0) {
                costFee = costFee.min(maxVolume);
            }

            final BigDecimal costFeeUsd = costFee;
            for (final ApmCostConfigDto config : costConfigList) {
                BigDecimal costAmount = calculateApmCostFee(cost, costFeeUsd, config);
                if (List.of(DirectionTypeEnum.REJECTED, DirectionTypeEnum.CHARGE_BACK_REJECTED)
                        .contains(cost.getDirectionType())) {
                    costAmount = costAmount.abs().negate();
                }

                costFeeList.add(TransactionCostVo.builder()
                        .feeName(config.getFeeName())
                        .feeType(config.getFeeType())
                        .feeGroup(config.getFeeGroup())
                        .volume(costAmount)
                        .currency(CurrencyEnum.USD)
                        .build());
            }

            // filter shopify_fee, tax, fx cost
            final List<TransactionCostVo> resultList = costFeeList.stream().filter(x ->
                            StringUtils.equalsIgnoreCase("SHOPIFY_FEE", x.getFeeName())
                                    || FeeGroupEnum.TRANSACTION_FEE != x.getFeeGroup())
                    .collect(Collectors.toList());

            // merge transaction fee into resultList
            resultList.add(TransactionCostVo.builder()
                    .feeName(FeeTypeCodeEnum.TRANSACTION_FEE.getName())
                    .feeType(FeeTypeCodeEnum.TRANSACTION_FEE)
                    .feeGroup(FeeGroupEnum.TRANSACTION_FEE)
                    .volume(costFeeUsd)
                    .currency(CurrencyEnum.USD)
                    .build());

            final List<ExtraIncomeConfigurationDto> costExtraFeeConfig =
                    this.queryExtraIncomeConfig(cost, extraFeeConfig);

            final List<TransactionExtraFeeVo> extraFeeList =
                    this.calculateExtraIncome(cost, costExtraFeeConfig);

            // Rebuild CostInfo
            this.rebuildTransactionCostInfo(cost, resultList, extraFeeList);
        }

        // update to database
        this.batchUpdateTransactionCost(costList);
    }

    private void recalculateCardCost(
            final List<RecalculateCostData> costList,
            final CardCostConfigurationDto cardCostConfigDto,
            final List<ExtraIncomeConfigurationDto> extraFeeConfig) {

        if (CollectionUtils.isEmpty(costList)
                || Objects.isNull(cardCostConfigDto)
                || CollectionUtils.isEmpty(cardCostConfigDto.getCardCostConfigList())) {
            return;
        }

        for (final RecalculateCostData cost : costList) {

            final CardCostConfigurationDto cardCostConfig = CardCostConfigurationDto.builder()
                    .cardCostConfigList(cardCostConfigDto.getCardCostConfigList())
                    .build();

            // if order is refund or charge_back exclude shopify cost fee
            if (REFUND_CHARGE_BACK_TYPES.contains(cost.getDirectionType())) {
                this.excludeShopifyFeeRules(cardCostConfig);
            }

            final ExtendData extendData = transactionMoneyService.queryExtendInfo(
                    cost.getTransactionId(), cost.getDirectionType());

            final List<CardCostConfigDto> costConfigDataList =
                    this.getCardCostConfig(CardCostConfigBo.builder()
                            .accountId(cost.getAccountId())
                            .countryCode(cost.getCountryCode())
                            .vendor(cost.getVendor())
                            .directionType(cost.getDirectionType())
                            .cardType(getCardType(extendData))
                            .cardGroup(getCardGroup(extendData))
                            .installment(getInstallment(extendData))
                            .build(), cardCostConfig);

            final List<TransactionCostVo> costFeeList = Lists.newArrayList();
            final Map<FeeGroupEnum, List<CardCostConfigDto>> configGroup =
                    costConfigDataList.stream()
                            .collect(Collectors.groupingBy(CardCostConfigDto::getFeeGroup));

            // calculate FeeGroup == TRANSACTION_FEE
            final TransactionCostVo costFee = this.calculateCardCostFee(
                    cost, configGroup.get(FeeGroupEnum.TRANSACTION_FEE), extendData);
            if (Objects.nonNull(costFee)) {
                costFeeList.add(costFee);
            }

            // calculate FeeGroup != TRANSACTION_FEE, eg. Tax, Fx
            for (final Map.Entry<FeeGroupEnum, List<CardCostConfigDto>> entry :
                    configGroup.entrySet()) {
                if (FeeGroupEnum.TRANSACTION_FEE != entry.getKey()) {
                    for (final CardCostConfigDto config : entry.getValue()) {
                        costFeeList.add(TransactionCostVo.builder()
                                .feeName(config.getFeeName())
                                .feeType(config.getFeeType())
                                .feeGroup(config.getFeeGroup())
                                .volume(calculateCardCost(cost, costFee, config))
                                .currency(CurrencyEnum.USD)
                                .build());
                    }
                }
            }

            final List<ExtraIncomeConfigurationDto> costExtraFeeConfig
                    = queryExtraIncomeConfig(cost, extraFeeConfig);
            final List<TransactionExtraFeeVo> extraFeeList
                    = this.calculateExtraIncome(cost, costExtraFeeConfig);

            // Rebuild CostInfo
            this.rebuildTransactionCostInfo(cost, costFeeList, extraFeeList);

        }

        // update to database
        this.batchUpdateTransactionCost(costList);
    }

    private TransactionCostVo calculateCardCostFee(
            final RecalculateCostData cost,
            final List<CardCostConfigDto> costConfigList,
            final ExtendData extendData) {

        BigDecimal costAmount = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(costConfigList)) {
            return null;
        }

        BigDecimal minVolume = BigDecimal.ZERO;
        BigDecimal maxVolume = BigDecimal.ZERO;
        // transaction-fee
        for (final CardCostConfigDto costConfig : costConfigList) {
            if (FeeTypeCodeEnum.TRANSACTION_FEE == costConfig.getFeeType()) {
                minVolume = Optional.ofNullable(costConfig.getMinVolume()).orElse(BigDecimal.ZERO);
                maxVolume = Optional.ofNullable(costConfig.getMaxVolume()).orElse(BigDecimal.ZERO);
                if (minVolume.compareTo(BigDecimal.ZERO) > 0) {
                    minVolume = CurrencyEnum.USD == costConfig.getCurrency()
                            ? minVolume : AmountUtil.division(minVolume, cost.getFxRate(), 6);
                }
                if (maxVolume.compareTo(BigDecimal.ZERO) > 0) {
                    maxVolume = CurrencyEnum.USD == costConfig.getCurrency()
                            ? maxVolume : AmountUtil.division(maxVolume, cost.getFxRate(), 6);
                }

                costAmount = costAmount.add(calculateCardCost(cost, null, costConfig));
            }
        }

        if (minVolume.compareTo(BigDecimal.ZERO) > 0) {
            costAmount = costAmount.max(minVolume);
        }
        if (maxVolume.compareTo(BigDecimal.ZERO) > 0) {
            costAmount = costAmount.min(maxVolume);
        }

        // other_transaction_fee( 3DS_FEE, ANTI_FRAUD_FEE )
        for (final CardCostConfigDto costConfig : costConfigList) {
            if (FeeTypeCodeEnum.TRANSACTION_FEE == costConfig.getFeeType()) {
                continue;
            }

            // 3DS_FEE
            if (Optional.ofNullable(extendData.getCardUse3ds()).orElse(Boolean.FALSE)
                    && FeeTypeCodeEnum.THREE_DS_FEE == costConfig.getFeeType()) {
                costAmount = costAmount.add(calculateCardCost(cost, null, costConfig));
            }

            // ANTI_FRAUD_FEE
            // TODO nti-fraud fee rule to be confirmed;
        }

        return TransactionCostVo.builder()
                .feeName(FeeTypeCodeEnum.TRANSACTION_FEE.getName())
                .feeType(FeeTypeCodeEnum.TRANSACTION_FEE)
                .feeGroup(FeeGroupEnum.TRANSACTION_FEE)
                .volume(costAmount)
                .currency(CurrencyEnum.USD)
                .build();
    }

    private static BigDecimal calculateCardCost(final RecalculateCostData cost,
                                                final TransactionCostVo costFee,
                                                final CardCostConfigDto costConfig) {
        // Using Fixed-Cost
        if (FeeValueModelEnum.FIXED == costConfig.getFeeModel()) {
            return CurrencyEnum.USD == costConfig.getCurrency() ? costConfig.getVolume()
                    : AmountUtil.division(costConfig.getVolume(), cost.getFxRate(), 6);
        }

        // Using Percentage-Cost
        final BigDecimal amount = FeeOnEnum.AMOUNT == costConfig.getFeeOn()
                ? cost.getAmountUsd().abs()
                : Optional.ofNullable(costFee)
                .map(x -> x.getVolume().abs()).orElse(BigDecimal.ZERO);

        return amount.multiply(costConfig.getVolume()).setScale(6, RoundingMode.HALF_UP);
    }

    private int getInstallment(final ExtendData extendData) {
        if (Objects.isNull(extendData) || Objects.isNull(extendData.getCardInstallments())) {
            return 0;
        }

        return Optional.of(extendData.getCardInstallments())
                .orElse(0) <= 1 ? 0 : extendData.getCardInstallments();
    }

    private CreditCardGroupCodeEnum getCardGroup(final ExtendData extendData) {
        if (Objects.isNull(extendData) || StringUtils.isBlank(extendData.getCardBrand())) {
            return CreditCardGroupCodeEnum.DEFAULT;
        }

        return CreditCardGroupCodeEnum.parse(extendData.getCardBrand());
    }

    private CardTypeEnum getCardType(final ExtendData extendData) {
        if (Objects.isNull(extendData) || StringUtils.isBlank(extendData.getCardType())) {
            return CardTypeEnum.CREDIT_CARD;
        }

        return CardTypeEnum.parse(extendData.getCardType().trim());
    }

    private List<TransactionExtraFeeVo> calculateExtraIncome(
            final RecalculateCostData cost,
            final List<ExtraIncomeConfigurationDto> extraFeeConfig) {
        if (CollectionUtils.isEmpty(extraFeeConfig)) {
            return Collections.emptyList();
        }

        BigDecimal costFeeUsd = BigDecimal.ZERO;
        for (final ExtraIncomeConfigurationDto dto : extraFeeConfig) {
            if (FeeTypeCodeEnum.TRANSACTION_FEE == dto.getFeeType()) {
                costFeeUsd = costFeeUsd.add(calculateExtraFee(cost, BigDecimal.ZERO, dto));
            }
        }

        final BigDecimal finalCostFeeUsd = costFeeUsd;
        return extraFeeConfig.stream().map(config -> TransactionExtraFeeVo.builder()
                .extraFeeGroup(config.getFeeGroup())
                .volume(calculateExtraFee(cost, finalCostFeeUsd, config))
                .build()).collect(Collectors.toList());
    }

    private static BigDecimal calculateExtraFee(final RecalculateCostData cost,
                                                final BigDecimal costFeeUsd,
                                                final ExtraIncomeConfigurationDto extraFeeConfig) {
        // Using Fixed-Cost
        if (FeeValueModelEnum.FIXED == extraFeeConfig.getFeeModel()) {
            return CurrencyEnum.USD == extraFeeConfig.getCurrency() ? extraFeeConfig.getVolume()
                    : AmountUtil.division(extraFeeConfig.getVolume(), cost.getFxRate(), 6);
        }

        // Using Percentage-Cost
        final BigDecimal amount = FeeOnEnum.AMOUNT == extraFeeConfig.getFeeOn()
                ? cost.getAmountUsd().abs() : costFeeUsd.abs();

        return amount.multiply(extraFeeConfig.getVolume()).setScale(6, RoundingMode.HALF_UP);
    }

    private List<ExtraIncomeConfigurationDto> queryExtraIncomeConfig(
            final RecalculateCostData cost,
            final List<ExtraIncomeConfigurationDto> extraIncomeConfig) {

        if (DirectionTypeEnum.REJECTED == cost.getDirectionType()) {
            return Collections.emptyList();
        }

        if (TransactionTypeCodeEnum.PAY_IN == cost.getTransactionTypeCode()
                && ProductCodeEnum.CARD == cost.getProductCode()) {
            final ExtendData extendData = transactionMoneyService.queryExtendInfo(
                    cost.getTransactionId(), cost.getDirectionType());

            final CardTypeEnum cardType = Optional.ofNullable(getCardType(extendData))
                    .orElse(CardTypeEnum.CREDIT_CARD);
            final CreditCardGroupCodeEnum carGroup = getCardGroup(extendData);
            final int installments = getInstallment(extendData);

            // Base config list
            final List<ExtraIncomeConfigurationDto> baseConfigList = extraIncomeConfig.stream()
                    .filter(x -> x.getProductCode() == cost.getProductCode())
                    .filter(x -> installments >= x.getInstallmentBegin()
                            && installments <= x.getInstallmentEnd())
                    .collect(Collectors.toList());

            // filter with cardType and cardGroup
            List<ExtraIncomeConfigurationDto> strictList = baseConfigList.stream()
                    .filter(x -> x.getCardType() == cardType)
                    .filter(x -> x.getCardGroup() == carGroup)
                    .collect(Collectors.toList());

            // Filter with default cardGroup
            if (CollectionUtils.isEmpty(strictList)) {
                strictList = baseConfigList.stream()
                        .filter(x -> x.getCardType() == cardType)
                        .filter(x -> CreditCardGroupCodeEnum.DEFAULT == x.getCardGroup())
                        .collect(Collectors.toList());
            }

            return CollectionUtils.isNotEmpty(strictList) ? strictList : baseConfigList;
        }

        // If the configured product-code is empty, it means using all product types
        final List<ExtraIncomeConfigurationDto> applicableToAllProductsConfig =
                extraIncomeConfig.stream()
                        .filter(item -> Objects.isNull(item.getProductCode()))
                        .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(applicableToAllProductsConfig)) {
            return applicableToAllProductsConfig;
        }

        // Obtain the specified product-code configuration
        return extraIncomeConfig.stream()
                .filter(e -> e.getProductCode() == cost.getProductCode())
                .collect(Collectors.toList());
    }

    private List<CardCostConfigDto> getCardCostConfig(
            final CardCostConfigBo bo,
            final CardCostConfigurationDto cardCostConfig) {

        if (Objects.isNull(cardCostConfig)
                || CollectionUtils.isEmpty(cardCostConfig.getCardCostConfigList())) {
            return Collections.emptyList();
        }

        final List<CardCostConfigDto> configList = cardCostConfig.getCardCostConfigList();
        final Set<CreditCardGroupCodeEnum> allCardGroupList = configList.stream()
                .map(CardCostConfigDto::getCardGroup).collect(Collectors.toSet());

        // If the card-group does not have cost rules defined, use the DEFAULT configuration
        bo.setCardGroup(allCardGroupList.contains(bo.getCardGroup())
                ? bo.getCardGroup() : CreditCardGroupCodeEnum.DEFAULT);

        /* universal transaction_fee config */
        final List<CardCostConfigDto> costConfigList = configList.stream()
                .filter(item -> (Optional.ofNullable(item.getAccountId()).orElse(0L).equals(0L)
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup()
                        && item.getCountryCode() == bo.getCountryCode()
                        && item.getVendorCode() == bo.getVendor()
                        && item.getDirectionType() == bo.getDirectionType()
                        && item.getCardType() == bo.getCardType()
                        && item.getCardGroup() == bo.getCardGroup()
                        && bo.getInstallment() >= item.getInstallmentBegin()
                        && bo.getInstallment() <= item.getInstallmentEnd()))
                .collect(Collectors.toList());

        final List<FeeGroupEnum> taxFxFee = List.of(FeeGroupEnum.TAX, FeeGroupEnum.FX);
        /* customize(TAX, FX) config */
        List<CardCostConfigDto> customizeTaxFxList = configList.stream().filter(item ->
                        (bo.getAccountId().equals(item.getAccountId())
                                && taxFxFee.contains(item.getFeeGroup())
                                && bo.getInstallment() >= item.getInstallmentBegin()
                                && bo.getInstallment() <= item.getInstallmentEnd()))
                .collect(Collectors.toList());

        /* if no customize(TAX, FX), use default config */
        if (CollectionUtils.isEmpty(customizeTaxFxList)) {
            customizeTaxFxList = configList.stream().filter(item ->
                            (Optional.ofNullable(item.getAccountId()).orElse(0L).equals(0L)
                                    && taxFxFee.contains(item.getFeeGroup())
                                    && bo.getCountryCode() == item.getCountryCode()
                                    && bo.getInstallment() >= item.getInstallmentBegin()
                                    && bo.getInstallment() <= item.getInstallmentEnd()))
                    .collect(Collectors.toList());
        }

        /* merge transactionFeeList and customizeTaxFxList */
        costConfigList.addAll(customizeTaxFxList);

        /* merge customize supplementary revenue */
        costConfigList.addAll(configList.stream().filter(item ->
                        (bo.getAccountId().equals(item.getAccountId())
                                && bo.getInstallment() >= item.getInstallmentBegin()
                                && bo.getInstallment() <= item.getInstallmentEnd()))
                .collect(Collectors.toList()));

        // deduplication
        return Lists.newArrayList(costConfigList.stream()
                .collect(Collectors.toMap(CardCostConfigDto::getId,
                        Function.identity(), (x, y) -> x)).values());
    }

    private void rebuildTransactionCostInfo(
            final RecalculateCostData cost,
            final List<TransactionCostVo> costFeeList,
            final List<TransactionExtraFeeVo> extraFeeList) {

        final BusinessStrategyEnum businessStrategy = BusinessStrategyEnum.parse(
                cost.getTransactionTypeCode(), cost.getDirectionType());

        // transaction cost fee
        BigDecimal costFee = BigDecimal.ZERO;
        // tax cost
        BigDecimal costTax = BigDecimal.ZERO;
        // fx cost
        BigDecimal costFx = BigDecimal.ZERO;
        // other cost
        BigDecimal costOther = BigDecimal.ZERO;
        BigDecimal fxLoseUsd = Optional.ofNullable(cost.getFxLose()).orElse(BigDecimal.ZERO);
        // BigDecimal fxUsd = (cost.getAmountUsd().abs().multiply(fxLoseUsd)).negate();
        BigDecimal fxUsd = (cost.getAmountUsd().abs().multiply(fxLoseUsd))
                .multiply(businessStrategy.getFeePon().getCode());

        // extra transaction revenue fee
        BigDecimal extraFeeUsd = BigDecimal.ZERO;
        BigDecimal extraTaxUsd = BigDecimal.ZERO;
        BigDecimal extraFxUsd = BigDecimal.ZERO;

        BigDecimal configFxUsd = BigDecimal.ZERO;
        BigDecimal configFxLose = BigDecimal.ZERO;

        boolean fxUsdUseConfig = false;
        boolean fxLoseUseConfig = false;

        for (final TransactionCostVo vo : costFeeList) {
            final BigDecimal volume = Optional.ofNullable(vo.getVolume()).orElse(BigDecimal.ZERO);
            // Assess the cost
            if (FeeGroupEnum.TRANSACTION_FEE == vo.getFeeGroup()) {
                costFee = costFee.add(volume);
            } else if (FeeGroupEnum.TAX == vo.getFeeGroup()) {
                costTax = costTax.add(volume);
            } else if (FeeGroupEnum.FX == vo.getFeeGroup()) {
                costFx = costFx.add(volume);
            } else {
                costOther = costOther.add(volume);
            }
        }

        for (final TransactionExtraFeeVo vo : extraFeeList) {
            final BigDecimal volume = vo.getVolume();
            if (vo.getExtraFeeGroup().equals(ExtraFeeGroupEnum.EXTRA_FEE)) {
                extraFeeUsd = extraFeeUsd.add(volume);
            } else if (vo.getExtraFeeGroup().equals(ExtraFeeGroupEnum.EXTRA_TAX)) {
                extraTaxUsd = extraTaxUsd.add(volume);
            } else if (vo.getExtraFeeGroup().equals(ExtraFeeGroupEnum.EXTRA_FX)) {
                extraFxUsd = extraFxUsd.add(volume);
            }

            if (vo.getExtraFeeGroup().equals(ExtraFeeGroupEnum.FX)) {
                fxUsdUseConfig = true;
                configFxUsd = configFxUsd.add(volume);
            } else if (vo.getExtraFeeGroup().equals(ExtraFeeGroupEnum.FX_LOSE)) {
                fxLoseUseConfig = true;
                configFxLose = configFxLose.add(volume);
            }
        }

        if (fxUsdUseConfig) {
            fxUsd = configFxUsd.multiply(businessStrategy.getFeePon().getCode());
        }
        if (fxLoseUseConfig) {
            fxLoseUsd = configFxLose;
        }

        /* When Order.directionType == REFUND then extra_xxx=0, taxUsd=0, costTax=0,
           fx_usd.negate, cost_fx.negate */
        if (DirectionTypeEnum.REFUND == cost.getDirectionType()
                || DirectionTypeEnum.CHARGE_BACK == cost.getDirectionType()
                || DirectionTypeEnum.CHARGE_BACK_REJECTED == cost.getDirectionType()) {
            cost.setTaxUsd(BigDecimal.ZERO);
            extraFeeUsd = BigDecimal.ZERO;
            extraTaxUsd = BigDecimal.ZERO;
            extraFxUsd = BigDecimal.ZERO;

            costTax = BigDecimal.ZERO;

            fxUsd = fxUsd.negate();
            costFx = costFx.negate();
        }

        cost.setFxUsd(fxUsd);
        cost.setFxLose(fxLoseUsd);

        cost.setExtraFeeUsd(extraFeeUsd.multiply(businessStrategy.getFeePon().getCode()));
        cost.setExtraTaxUsd(extraTaxUsd.multiply(businessStrategy.getFeePon().getCode()));
        cost.setExtraFxUsd(extraFxUsd.multiply(businessStrategy.getFeePon().getCode()));

        cost.setCostFee(costFee);
        cost.setCostTax(costTax);
        cost.setCostFx(costFx);
        cost.setCostOther(costOther);
    }

    private static BigDecimal calculateApmCostFee(
            final RecalculateCostData cost,
            final BigDecimal costFeeUsd,
            final ApmCostConfigDto costConfig) {

        // Using Fixed-Cost
        if (FeeValueModelEnum.FIXED == costConfig.getFeeModel()) {
            return CurrencyEnum.USD == costConfig.getCurrency()
                    ? costConfig.getVolume()
                    : costConfig.getVolume()
                    .divide(cost.getFxRate(), 6, RoundingMode.HALF_UP);
        }

        // Using Percentage-Cost
        final BigDecimal amount = FeeOnEnum.AMOUNT == costConfig.getFeeOn()
                ? cost.getAmountUsd().abs() : costFeeUsd.abs();

        // Using EXCLUDING TAX
        return amount.multiply(costConfig.getVolume()).setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void syncCostBillId(final Long accountId, final Long billId) {
        if (Optional.ofNullable(accountId).orElse(0L) <= 0
                || Optional.ofNullable(billId).orElse(0L) <= 0) {
            return;
        }

        repository.batchUpdateCostBillId(accountId, billId);
    }
}
