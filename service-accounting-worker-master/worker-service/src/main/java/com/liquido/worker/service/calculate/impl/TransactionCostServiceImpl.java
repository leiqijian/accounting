package com.liquido.worker.service.calculate.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.base.pojo.dto.ApmCostConfigDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.worker.pojo.bo.PreCalculateConfigBo;
import com.liquido.worker.pojo.bo.TransactionCostBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.service.calculate.TransactionCostService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class TransactionCostServiceImpl implements TransactionCostService {

    @Override
    public List<TransactionCostBo> calculateTransactionCost(
            final TransactionMoneyBo moneyBo,
            final PreCalculateConfigBo preConfig,
            final DailyExchangeRateDto usdRateInfo) {

        if (List.of(DirectionTypeEnum.REJECTED, DirectionTypeEnum.CHARGE_BACK_REJECTED)
                .contains(moneyBo.getDirectionType())
                || Objects.isNull(moneyBo.getVendor())
                || VendorCodeEnum.UNKNOWN == moneyBo.getVendor()) {
            return Collections.emptyList();
        }

        // card(credit-card, debit-card) transactions
        if (ProductCodeEnum.CARD == moneyBo.getProductCode()) {
            return calculateCardTypeCostFee(moneyBo, usdRateInfo, preConfig.getCardCostConfig());
        }

        // apm transactions
        return calculateApmCostFee(moneyBo, usdRateInfo, preConfig.getApmCostConfig());
    }

    private List<TransactionCostBo> calculateCardTypeCostFee(
            final TransactionMoneyBo moneyBo,
            final DailyExchangeRateDto usdRateInfo,
            final CardCostConfigurationDto cardCostConfig) {

        if (Objects.isNull(cardCostConfig)
                || CollectionUtils.isEmpty(cardCostConfig.getCardCostConfigList())) {
            return Collections.emptyList();
        }

        final List<TransactionCostBo> costFeeList = Lists.newArrayList();
        final Map<FeeGroupEnum, List<CardCostConfigDto>> configGroup =
                cardCostConfig.getCardCostConfigList().stream()
                        .collect(Collectors.groupingBy(CardCostConfigDto::getFeeGroup));

        // calculate FeeGroup == TRANSACTION_FEE
        final TransactionCostBo costFee = this.calculateCardCostFee(
                moneyBo, usdRateInfo, configGroup.get(FeeGroupEnum.TRANSACTION_FEE));
        if (Objects.nonNull(costFee)) {
            costFeeList.add(costFee);
        }

        // calculate FeeGroup != TRANSACTION_FEE, eg. Tax, Fx
        for (final Map.Entry<FeeGroupEnum, List<CardCostConfigDto>> entry :
                configGroup.entrySet()) {
            if (FeeGroupEnum.TRANSACTION_FEE != entry.getKey()) {
                for (final CardCostConfigDto costConfig : entry.getValue()) {
                    costFeeList.add(TransactionCostBo.builder()
                            .feeName(costConfig.getFeeName())
                            .feeType(costConfig.getFeeType())
                            .feeGroup(costConfig.getFeeGroup())
                            .volume(calculateCardCost(moneyBo, costFee, costConfig, usdRateInfo))
                            .currency(CurrencyEnum.USD)
                            .build());
                }
            }
        }

        return costFeeList;
    }

    private TransactionCostBo calculateCardCostFee(
            final TransactionMoneyBo moneyBo,
            final DailyExchangeRateDto usdRateInfo,
            final List<CardCostConfigDto> costConfigList) {

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
                    minVolume = CurrencyEnum.USD == costConfig.getCurrency() ? minVolume
                            : AmountUtil.division(minVolume, usdRateInfo.getMerchantRate(), 6);
                }
                if (maxVolume.compareTo(BigDecimal.ZERO) > 0) {
                    maxVolume = CurrencyEnum.USD == costConfig.getCurrency() ? maxVolume
                            : AmountUtil.division(maxVolume, usdRateInfo.getMerchantRate(), 6);
                }

                costAmount = costAmount.add(
                        calculateCardCost(moneyBo, null, costConfig, usdRateInfo));
            }
        }

        // use min fee
        if (minVolume.compareTo(BigDecimal.ZERO) > 0) {
            costAmount = costAmount.max(minVolume);
        }
        if (maxVolume.compareTo(BigDecimal.ZERO) > 0) {
            costAmount = costAmount.min(maxVolume);
        }

        // other-transaction-fee( 3DS_FEE, ANTI_FRAUD_FEE )
        for (final CardCostConfigDto costConfig : costConfigList) {
            if (FeeTypeCodeEnum.TRANSACTION_FEE == costConfig.getFeeType()) {
                continue;
            }

            // 3DS_FEE
            if (checkUse3ds(moneyBo.getExtendData())
                    && FeeTypeCodeEnum.THREE_DS_FEE == costConfig.getFeeType()) {
                costAmount = costAmount.add(
                        calculateCardCost(moneyBo, null, costConfig, usdRateInfo));
            }

            // ANTI_FRAUD_FEE
            // TODO rule to be confirmed;
        }

        return TransactionCostBo.builder()
                .feeName(FeeTypeCodeEnum.TRANSACTION_FEE.getName())
                .feeType(FeeTypeCodeEnum.TRANSACTION_FEE)
                .feeGroup(FeeGroupEnum.TRANSACTION_FEE)
                .volume(costAmount)
                .currency(CurrencyEnum.USD)
                .build();
    }

    /**
     * use 3ds authentication
     *
     * @param others
     *
     * @return
     */
    private static boolean checkUse3ds(final JsonNode others) {
        if (Objects.nonNull(others)) {
            final JsonNode cardUse3ds = others.get("cardUse3ds");
            if (Objects.nonNull(cardUse3ds)) {
                return cardUse3ds.asBoolean(false);
            }
        }

        return false;
    }

    private List<TransactionCostBo> calculateApmCostFee(
            final TransactionMoneyBo moneyBo,
            final DailyExchangeRateDto usdRateInfo,
            final ApmCostConfigurationDto apmCostConfig) {

        if (CollectionUtils.isEmpty(apmCostConfig.getCostConfigList())) {
            return Collections.emptyList();
        }

        final BigDecimal minVolume = apmCostConfig.getCostConfigList().stream()
                .filter(x -> FeeTypeCodeEnum.TRANSACTION_FEE == x.getFeeType())
                .map(x -> CurrencyEnum.USD == x.getCurrency()
                        ? x.getMinVolume()
                        : AmountUtil.division(x.getMinVolume(), usdRateInfo.getMerchantRate(), 6))
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        final BigDecimal maxVolume = apmCostConfig.getCostConfigList().stream()
                .filter(x -> FeeTypeCodeEnum.TRANSACTION_FEE == x.getFeeType())
                .map(x -> CurrencyEnum.USD == x.getCurrency()
                        ? x.getMaxVolume()
                        : AmountUtil.division(x.getMaxVolume(), usdRateInfo.getMerchantRate(), 6))
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        BigDecimal costAmount = apmCostConfig.getCostConfigList().stream()
                .filter(x -> FeeGroupEnum.TRANSACTION_FEE == x.getFeeGroup())
                .filter(x -> !StringUtils.equalsIgnoreCase("SHOPIFY_FEE", x.getFeeName()))
                .map(x -> calculateApmCost(moneyBo, BigDecimal.ZERO, x, usdRateInfo))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (minVolume.compareTo(BigDecimal.ZERO) > 0) {
            costAmount = costAmount.max(minVolume);
        }
        if (maxVolume.compareTo(BigDecimal.ZERO) > 0) {
            costAmount = costAmount.min(maxVolume);
        }

        final BigDecimal finalCostAmount = costAmount;

        // forEach cost config to calculate cost
        final List<TransactionCostBo> costDetails = apmCostConfig.getCostConfigList().stream()
                .map(x -> TransactionCostBo.builder()
                        .feeName(x.getFeeName())
                        .feeType(x.getFeeType())
                        .feeGroup(x.getFeeGroup())
                        .volume(calculateApmCost(moneyBo, finalCostAmount, x, usdRateInfo))
                        .currency(CurrencyEnum.USD)
                        .build()).collect(Collectors.toList());

        // filter shopify_fee, tax, fx cost
        final List<TransactionCostBo> resultList = costDetails.stream().filter(x ->
                        StringUtils.equalsIgnoreCase("SHOPIFY_FEE", x.getFeeName())
                                || FeeGroupEnum.TRANSACTION_FEE != x.getFeeGroup())
                .collect(Collectors.toList());

        // merge transaction fee into resultList
        resultList.add(TransactionCostBo.builder()
                .feeName(FeeTypeCodeEnum.TRANSACTION_FEE.getName())
                .feeType(FeeTypeCodeEnum.TRANSACTION_FEE)
                .feeGroup(FeeGroupEnum.TRANSACTION_FEE)
                .volume(finalCostAmount)
                .currency(CurrencyEnum.USD)
                .build());

        return resultList;
    }

    private static BigDecimal calculateApmCost(
            final TransactionMoneyBo moneyBo,
            final BigDecimal costFeeUsd,
            final ApmCostConfigDto costConfig,
            final DailyExchangeRateDto usdRateInfo) {

        // Using Fixed-Cost
        if (FeeValueModelEnum.FIXED == costConfig.getFeeModel()) {
            return CurrencyEnum.USD == costConfig.getCurrency() ? costConfig.getVolume()
                    : AmountUtil.division(costConfig.getVolume(), usdRateInfo.getMerchantRate(), 6);
        }

        // Using Percentage-Cost
        final BigDecimal amount = FeeOnEnum.AMOUNT == costConfig.getFeeOn()
                ? moneyBo.getSettlementAmountUsd().abs() : costFeeUsd.abs();

        return amount.multiply(costConfig.getVolume()).setScale(6, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateCardCost(final TransactionMoneyBo moneyBo,
                                                final TransactionCostBo costFee,
                                                final CardCostConfigDto costConfig,
                                                final DailyExchangeRateDto usdRateInfo) {
        // Using Fixed-Cost
        if (FeeValueModelEnum.FIXED == costConfig.getFeeModel()) {
            return CurrencyEnum.USD == costConfig.getCurrency() ? costConfig.getVolume()
                    : AmountUtil.division(costConfig.getVolume(), usdRateInfo.getMerchantRate(), 6);
        }

        // Using Percentage-Cost
        final BigDecimal amount = FeeOnEnum.AMOUNT == costConfig.getFeeOn()
                ? moneyBo.getSettlementAmountUsd().abs()
                : (Optional.ofNullable(costFee).map(x -> x.getVolume().abs())
                .orElse(BigDecimal.ZERO));

        return amount.multiply(costConfig.getVolume()).setScale(6, RoundingMode.HALF_UP);
    }
}
