package com.liquido.statement.service.settlement.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.entity.TransactionCost;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.ExtendData;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.TransactionFeeVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.service.DailyExchangeRateService;
import com.liquido.statement.service.TransactionCostService;
import com.liquido.statement.service.TransactionFeeService;
import com.liquido.statement.service.TransactionMoneyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * From Bank Rejection and refunds
 * <p>
 * formula: refund all settlement amount and fee
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutRejectedHandler extends AbstractAccountSettlementHandler {
    private final BaseService baseService;
    private final ModelMapper modelMapper;
    private final TransactionFeeService transactionFeeService;
    private final TransactionCostService transactionCostService;
    private final TransactionMoneyService transactionMoneyService;
    private final DailyExchangeRateService dailyExchangeRateService;

    @Override
    public BusinessStrategyEnum getStrategy() {
        return BusinessStrategyEnum.PAY_OUT_REJECTED;
    }

    @Override
    public void beforeExecuteSettlement(final List<TransactionMoneyVo> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        for (final TransactionMoneyVo order : orderList) {
            // load reference original transaction order from db
            final TransactionMoney originalOrder = transactionMoneyService.queryOriginalOrderInfo(
                    order.getTransactionId(), DirectionTypeEnum.SETTLED);
            if (Objects.isNull(originalOrder)) {
                throw CommonExceptionCode.DATA_NOT_FOUND.exception();
            }

            // Reset order info
            order.setAmount(originalOrder.getAmount());
            order.setFxRateId(originalOrder.getFxRateId());
            order.setFxRate(originalOrder.getFxRate());
            order.setFxUsdRate(getMerchantUsdExchangeRate(order));
            order.setSettlementAmount(originalOrder.getSettlementAmount());
            order.setSettlementAmountUsd(originalOrder.getSettlementAmountUsd());

            // load reference original transaction fees
            final List<TransactionFee> originalFeeList =
                    transactionFeeService.findSettledFeeList(originalOrder.getTransactionId());
            if (CollectionUtils.isNotEmpty(originalFeeList)) {
                List<TransactionFeeVo> feeList = originalFeeList.stream()
                        .map(originalFee -> TransactionFeeVo.builder()
                                .feeConfigurationId(originalFee.getFeeConfigurationId())
                                .feeName(originalFee.getFeeName())
                                .feeTypeCode(originalFee.getFeeTypeCode())
                                .feeGroup(originalFee.getFeeGroup())
                                .amountPon(BusinessStrategyEnum.PAY_OUT_REJECTED.getFeePon())
                                .calculateAmount(originalFee.getCalculateAmount().abs())
                                .settlementAmount(originalFee.getSettlementAmount().abs())
                                .settlementCurrency(originalFee.getSettlementCurrency())
                                .settlementAmountUsd(BigDecimal.ZERO)
                                .instantFlag(Boolean.TRUE)
                                .build()).collect(Collectors.toList());

                if (CountryCodeEnum.CO.equals(order.getCountryCode())
                        && TransactionTypeCodeEnum.PAY_OUT.equals(order.getTransactionTypeCode())
                        && order.getIsRejectedDebit()) {
                    // CO PAY_OUT SETTLED -> REJECTED
                    // we will not refund the feeOn=Fee fee. like GMF
                    final List<Long> feeOnFeeMonthlyConfigIds =
                            baseService.queryFeeOnFeeMonthlyConfigByFeeIds(feeList.stream()
                                    .map(TransactionFeeVo::getFeeConfigurationId)
                                    .collect(Collectors.toList()));

                    feeList = feeList.stream()
                            .filter(fee -> {
                                if (fee.getFeeTypeCode().equals(FeeTypeCodeEnum.TRANSACTION_FEE)) {
                                    return false;
                                }
                                return !feeOnFeeMonthlyConfigIds
                                        .contains(fee.getFeeConfigurationId());
                            }).collect(Collectors.toList());
                }

                // reset fees
                order.setTransactionFeeList(feeList);
            }
        }
    }

    @Override
    public void postExecuteSettlement(final BatchAccountSettlementBo batchBo) {

        this.processPayoutRejectCost(batchBo);

        this.processOriginalOrderNonFinalSettleStatus(batchBo);

    }

    private void processPayoutRejectCost(final BatchAccountSettlementBo batchBo) {
        for (final TransactionMoney originalOrder : batchBo.getTransactionMoneyList()) {
            final TransactionCost originalCost = transactionCostService.queryTransactionCostInfo(
                    originalOrder.getAccountId(), originalOrder.getTransactionId(),
                    DirectionTypeEnum.SETTLED);
            if (Objects.isNull(originalCost)) {
                continue;
            }

            final TransactionCost rejectedCost = modelMapper.copy(originalCost);
            rejectedCost.setId(SnowflakeIdUtil.generate());
            rejectedCost.setDirectionType(DirectionTypeEnum.REJECTED);
            rejectedCost.setBillId(batchBo.getBillInitInfo().getBillId());

            rejectedCost.setAmount(originalCost.getAmount().negate());
            rejectedCost.setAmountUsd(originalCost.getAmountUsd().negate());
            rejectedCost.setFeeUsd(originalCost.getFeeUsd().negate());
            rejectedCost.setTaxUsd(originalCost.getTaxUsd().negate());
            rejectedCost.setFxUsd(originalCost.getFxUsd().negate());

            rejectedCost.setExtraFeeUsd(originalCost.getExtraFeeUsd().negate());
            rejectedCost.setExtraTaxUsd(originalCost.getExtraTaxUsd().negate());
            rejectedCost.setExtraFxUsd(originalCost.getExtraFxUsd().negate());

            rejectedCost.setCostFee(originalCost.getCostFee().negate());
            rejectedCost.setCostTax(originalCost.getCostTax().negate());
            rejectedCost.setCostFx(originalCost.getCostFx().negate());
            rejectedCost.setCostOther(originalCost.getCostOther().negate());

            rejectedCost.setTransactionTime(originalOrder.getTransactionTime());
            rejectedCost.setTransactionTimestamp(
                    LocalDateTimeUtil.utcToInstant(originalOrder.getTransactionTime()));
            rejectedCost.setCreatedTime(LocalDateTimeUtil.nowUtc());
            rejectedCost.setUpdatedTime(LocalDateTimeUtil.nowUtc());
            rejectedCost.setVersion(1);
            rejectedCost.setDelFlag(Boolean.FALSE);
            rejectedCost.setRemark("REJECTED");

            transactionCostService.saveTransactionCost(rejectedCost);
        }
    }

    private BigDecimal getMerchantUsdExchangeRate(final TransactionMoneyVo order) {
        if (CurrencyEnum.USD == order.getCurrency()) {
            return BigDecimal.ONE;
        }

        final DailyExchangeRateDto exchangeRate =
                dailyExchangeRateService.queryDailyExchangeRate(
                        QueryDailyExchangeRateVo.builder()
                                .merchantId(order.getMerchantId())
                                .accountId(order.getAccountId())
                                .sourceCurrency(CurrencyEnum.USD)
                                .targetCurrency(order.getCurrency())
                                .exchangeTime(order.getTransactionTime().withMinute(0)
                                        .withSecond(0).withNano(0))
                                .build());

        return exchangeRate.getMerchantRate();
    }

    // transaction calculated multiple times, add sign finalSettle status about original order
    private void processOriginalOrderNonFinalSettleStatus(final BatchAccountSettlementBo batchBo) {

        // find original order
        batchBo.getTransactionMoneyList().forEach(order -> {

            final TransactionMoney originalOrder = transactionMoneyService.queryOriginalOrderInfo(
                    order.getTransactionId(), DirectionTypeEnum.SETTLED);

            if (Objects.isNull(originalOrder)) {
                log.error("original transaction does not exist uniqueId={}, transactionId={}",
                        order.getUniqueId(), order.getTransactionId());
                throw CommonExceptionCode.DATA_NOT_FOUND.exception();
            }
            log.info("order directionType is={},originalOrder id={}",
                    order.getDirectionType().getCode(), originalOrder.getAccountId());

            ExtendData extendData =
                    Optional.ofNullable(originalOrder.getExtendData()).orElse(new ExtendData());

            extendData.setStateChange(true);

            originalOrder.setExtendData(extendData);

            transactionMoneyService.save(originalOrder);

        });
    }
}
