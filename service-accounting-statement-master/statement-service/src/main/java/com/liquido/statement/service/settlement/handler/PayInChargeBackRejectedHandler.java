package com.liquido.statement.service.settlement.handler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.entity.TransactionCost;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.TransactionFeeVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.service.TransactionCostService;
import com.liquido.statement.service.TransactionFeeService;
import com.liquido.statement.service.TransactionMoneyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * PayIn ChargeBack Rejected
 * formula: amount = +amount +fee
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayInChargeBackRejectedHandler extends AbstractAccountSettlementHandler {

    private final ModelMapper modelMapper;
    private final TransactionFeeService transactionFeeService;
    private final TransactionCostService transactionCostService;
    private final TransactionMoneyService transactionMoneyService;

    @Override
    public BusinessStrategyEnum getStrategy() {
        return BusinessStrategyEnum.PAY_IN_CHARGE_BACK_REJECTED;
    }

    @Override
    public void beforeExecuteSettlement(final List<TransactionMoneyVo> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        for (final TransactionMoneyVo order : orderList) {
            // load reference original transaction order from db
            final TransactionMoney originalOrder = transactionMoneyService.queryOriginalOrderInfo(
                    order.getTransactionId(), DirectionTypeEnum.CHARGE_BACK);
            if (Objects.isNull(originalOrder)) {
                log.error("original chargeback order not exist uniqueId={}, transactionId={}",
                        order.getUniqueId(), order.getTransactionId());
                throw CommonExceptionCode.DATA_NOT_FOUND.exception();
            }

            // Reset order info from original order
            order.setAmount(originalOrder.getAmount());
            order.setCurrency(originalOrder.getCurrency());
            order.setAmountPon(BusinessStrategyEnum.PAY_IN_CHARGE_BACK_REJECTED.getAmountPon());
            order.setFxRateId(originalOrder.getFxRateId());
            order.setFxRate(originalOrder.getFxRate());
            order.setFxUsdRate(originalOrder.getFxRateUsd());
            order.setFxUsdLose(originalOrder.getFxLoseUsd());
            order.setSettlementAmount(originalOrder.getSettlementAmount());
            order.setSettlementCurrency(originalOrder.getSettlementCurrency());
            order.setSettlementAmountUsd(originalOrder.getSettlementAmountUsd());
            order.setBeCreditedAmount(originalOrder.getBeCreditedAmount().negate());
            order.setHoldStatus(HoldStatusEnum.NORMAL);

            if (ProductCodeEnum.BOLETO == order.getProductCode()) {
                //Refill additionalCharge from original order
                order.setAdditionalCharge(Optional.ofNullable(originalOrder.getAdditionalCharge())
                        .orElse(Collections.emptyList()));
            }else{
                order.setAdditionalCharge(Collections.emptyList());
            }

            order.setTransactionCostList(Collections.emptyList());
            order.setTransactionFeeList(Collections.emptyList());

            // Reset fees from original order
            final List<TransactionFee> originalFeeList =
                    transactionFeeService.findTransactionFeeList(
                            originalOrder.getTransactionId(), DirectionTypeEnum.CHARGE_BACK);

            if (CollectionUtils.isNotEmpty(originalFeeList)) {
                final List<TransactionFeeVo> feeList = originalFeeList.stream()
                        .map(originalFee -> TransactionFeeVo.builder()
                                .feeConfigurationId(originalFee.getFeeConfigurationId())
                                .feeName(originalFee.getFeeName())
                                .feeTypeCode(originalFee.getFeeTypeCode())
                                .feeGroup(originalFee.getFeeGroup())
                                .amountPon(BusinessStrategyEnum.PAY_IN_CHARGE_BACK_REJECTED
                                        .getFeePon())
                                .calculateAmount(originalFee.getCalculateAmount().abs())
                                .settlementAmount(originalFee.getSettlementAmount().abs())
                                .settlementCurrency(originalFee.getSettlementCurrency())
                                .settlementAmountUsd(originalFee.getSettlementAmountUsd().abs())
                                .instantFlag(Boolean.TRUE)
                                .build()).collect(Collectors.toList());

                // reset fees
                order.setTransactionFeeList(feeList);
            }
        }
    }

    @Override
    public void prepareExecuteSettlement(final BatchAccountSettlementBo batchBo) {

        for (final TransactionMoney order : batchBo.getTransactionMoneyList()) {

            final TransactionMoney originalOrder = transactionMoneyService.queryOriginalOrderInfo(
                    order.getTransactionId(), DirectionTypeEnum.CHARGE_BACK);

            if (Objects.isNull(originalOrder)) {
                log.error("original transaction does not exist uniqueId={}, transactionId={}",
                        order.getUniqueId(), order.getTransactionId());
                throw CommonExceptionCode.DATA_NOT_FOUND.exception();
            }

            // current merchant account Date;
            final LocalDate currentMerchantDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                    .withZoneSameInstant(ZoneId.of(batchBo.getAccountSnapshot().getTimezone()))
                    .toLocalDate();

            // If the original order has not been credited, the waiting time for the refund order
            // to be credited remains the same as the credit time of the original order
            if (!TradingModelEnum.INSTANT_TRADING.contains(originalOrder.getTradingModel())
                    && originalOrder.getBeCreditedDate().isAfter(currentMerchantDate)) {

                //originalOrder.setHoldStatus(HoldStatusEnum.NORMAL);
                //originalOrder.setBeCreditedDate(order.getBeCreditedDate());
                //originalOrder.setTradingModel(order.getTradingModel());
                //batchBo.getNeedCreditOriginalMoneyList().add(originalOrder);
            }

            batchBo.getAllOriginalMoneyList().add(originalOrder);
        }
    }


    @Override
    public void postExecuteSettlement(final BatchAccountSettlementBo batchBo) {

        // setting beCreditDate of original order
        this.processOriginalOrderBeCreditDate(batchBo);

        // Reset transaction costs after settlement success
        this.processTransactionCostInfo(batchBo);
    }

    private void processOriginalOrderBeCreditDate(final BatchAccountSettlementBo batchBo) {
        //Update original transaction orders (beCreditedDate, holdStatus ...)
        if (CollectionUtils.isNotEmpty(batchBo.getNeedCreditOriginalMoneyList())) {
            transactionMoneyService.batchSave(batchBo.getNeedCreditOriginalMoneyList());
        }
    }

    private void processTransactionCostInfo(final BatchAccountSettlementBo batchBo) {
        for (final TransactionMoney order : batchBo.getTransactionMoneyList()) {

            final TransactionCost originalCost = transactionCostService.queryTransactionCostInfo(
                    order.getAccountId(), order.getTransactionId(),
                    DirectionTypeEnum.CHARGE_BACK);
            if (Objects.isNull(originalCost)) {
                continue;
            }

            final TransactionCost rejectedCost = modelMapper.copy(originalCost);
            rejectedCost.setId(SnowflakeIdUtil.generate());
            rejectedCost.setDirectionType(DirectionTypeEnum.CHARGE_BACK_REJECTED);
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

            rejectedCost.setTransactionTime(order.getTransactionTime());
            rejectedCost.setTransactionTimestamp(
                    LocalDateTimeUtil.utcToInstant(order.getTransactionTime()));
            rejectedCost.setCreatedTime(LocalDateTimeUtil.nowUtc());
            rejectedCost.setUpdatedTime(LocalDateTimeUtil.nowUtc());
            rejectedCost.setVersion(1);
            rejectedCost.setDelFlag(Boolean.FALSE);
            rejectedCost.setRemark("CHARGE_BACK_REJECTED");

            transactionCostService.saveTransactionCost(rejectedCost);
        }
    }

}
