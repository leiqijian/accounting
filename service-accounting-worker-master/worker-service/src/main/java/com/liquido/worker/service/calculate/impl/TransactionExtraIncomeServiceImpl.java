package com.liquido.worker.service.calculate.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.worker.feign.BaseService;
import com.liquido.worker.pojo.bo.TransactionExtraIncomeBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.service.calculate.CalculateManager;
import com.liquido.worker.service.calculate.TransactionExtraIncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionExtraIncomeServiceImpl implements TransactionExtraIncomeService {

    private final BaseService baseService;

    @Override
    public List<TransactionExtraIncomeBo> calculateExtraIncome(
            final TransactionMoneyBo moneyBo,
            final DailyExchangeRateDto usdRateInfo,
            final List<ExtraIncomeConfigurationDto> extraIncomeConfig) {

        if (CollectionUtils.isEmpty(extraIncomeConfig)) {
            return Collections.emptyList();
        }

        BigDecimal feeUsd = BigDecimal.ZERO;
        for (final ExtraIncomeConfigurationDto dto : extraIncomeConfig) {
            if (FeeTypeCodeEnum.TRANSACTION_FEE == dto.getFeeType()) {
                feeUsd = feeUsd.add(calculateExtraIncome(
                        moneyBo, BigDecimal.ZERO, dto, usdRateInfo));
            }
        }

        final BigDecimal finalFeeUsd = feeUsd;
        return extraIncomeConfig.stream().map(config -> TransactionExtraIncomeBo.builder()
                .extraFeeGroup(config.getFeeGroup())
                .volume(calculateExtraIncome(moneyBo, finalFeeUsd, config, usdRateInfo))
                .build()).collect(Collectors.toList());
    }

    private static BigDecimal calculateExtraIncome(
            final TransactionMoneyBo moneyBo,
            final BigDecimal feeUsd,
            final ExtraIncomeConfigurationDto extraFeeConfig,
            final DailyExchangeRateDto usdRateInfo) {

        // Using Fixed-Cost
        if (FeeValueModelEnum.FIXED == extraFeeConfig.getFeeModel()) {
            return CurrencyEnum.USD == extraFeeConfig.getCurrency() ? extraFeeConfig.getVolume()
                    : AmountUtil.division(extraFeeConfig.getVolume(),
                    usdRateInfo.getMerchantRate(), 6);
        }

        // Using Percentage-Cost
        final BigDecimal amount = FeeOnEnum.AMOUNT == extraFeeConfig.getFeeOn()
                ? moneyBo.getSettlementAmountUsd().abs() : feeUsd.abs();

        return amount.multiply(extraFeeConfig.getVolume()).setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public List<ExtraIncomeConfigurationDto> queryExtraIncomeConfig(
            final AccountDto accountInfo,
            final TaskFeeCalculation taskInfo) {

        if (List.of(DirectionTypeEnum.REJECTED, DirectionTypeEnum.CHARGE_BACK_REJECTED)
                .contains(taskInfo.getDirectionType())) {
            return Collections.emptyList();
        }

        final List<ExtraIncomeConfigurationDto> configList =
                baseService.queryExtraIncomeConfiguration(accountInfo.getId(),
                        taskInfo.getCountryCode(), taskInfo.getTransactionTypeCode());

        // card type of payin transaction(credit-card, debit-card)
        if (TransactionTypeCodeEnum.PAY_IN == taskInfo.getTransactionTypeCode()
                && ProductCodeEnum.CARD == taskInfo.getProductCode()) {

            final CardTypeEnum cardType = Optional.ofNullable(
                    CalculateManager.getCardType(taskInfo)).orElse(CardTypeEnum.CREDIT_CARD);
            final CreditCardGroupCodeEnum carGroup = CalculateManager.getCardBrand(taskInfo);
            final int installments = CalculateManager.getCardInstallments(taskInfo);

            // Base config list
            final List<ExtraIncomeConfigurationDto> baseConfigList = configList.stream()
                    .filter(x -> x.getProductCode() == taskInfo.getProductCode())
                    .filter(x -> installments >= x.getInstallmentBegin()
                            && installments <= x.getInstallmentEnd())
                    .collect(Collectors.toList());

            // Filter with cardType and cardGroup
            List<ExtraIncomeConfigurationDto> strictList = baseConfigList.stream()
                    .filter(e -> e.getCardType() == cardType)
                    .filter(e -> e.getCardGroup() == carGroup)
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
        final List<ExtraIncomeConfigurationDto> applicableToAllProductsConfig = configList.stream()
                .filter(item -> Objects.isNull(item.getProductCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(applicableToAllProductsConfig)) {
            return applicableToAllProductsConfig;
        }

        // Obtain the specified product-code configuration
        return configList.stream()
                .filter(e -> e.getProductCode() == taskInfo.getProductCode())
                .collect(Collectors.toList());
    }

}
