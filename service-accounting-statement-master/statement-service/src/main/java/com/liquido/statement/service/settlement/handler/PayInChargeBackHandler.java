package com.liquido.statement.service.settlement.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.enums.TransactionChargeBackStatusEnum;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.entity.TransactionChargeBackOrder;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.vo.ExtendData;
import com.liquido.statement.service.TransactionChargeBackOrderService;
import com.liquido.statement.service.TransactionMoneyService;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * From Bank Rejection and refunds
 * <p>
 * formula: amount = amount - (tradeAmount * fx) - fee
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayInChargeBackHandler extends AbstractAccountSettlementHandler {
    private final TransactionMoneyService transactionMoneyService;
    private final TransactionChargeBackOrderService transactionChargeBackOrderService;
    private final StatementProperties.ChargeBackOrder chargeBackOrderProperties;

    @Override
    public BusinessStrategyEnum getStrategy() {
        return BusinessStrategyEnum.PAY_IN_CHARGE_BACK;
    }

    @Override
    public void prepareExecuteSettlement(final BatchAccountSettlementBo batchBo) {

        for (final TransactionMoney order : batchBo.getTransactionMoneyList()) {

            // obtain reference original transaction order from db
            final TransactionMoney originalOrder = transactionMoneyService.queryOriginalOrderInfo(
                    order.getTransactionId(), DirectionTypeEnum.SETTLED);

            if (Objects.isNull(originalOrder)) {
                log.error("original transaction does not exist uniqueId={}, transactionId={}",
                        order.getUniqueId(), order.getTransactionId());
                throw CommonExceptionCode.DATA_NOT_FOUND.exception();
            }

            // transaction calculated multiple times,
            // add sign final settle status about original order
            final ExtendData extendData = Optional.ofNullable(originalOrder.getExtendData())
                    .orElse(new ExtendData());
            extendData.setStateChange(Boolean.TRUE);
            originalOrder.setExtendData(extendData);

            if (ProductCodeEnum.BOLETO == order.getProductCode()) {
                //Refill additionalCharge from original order
                order.setAdditionalCharge(Optional.ofNullable(originalOrder.getAdditionalCharge())
                        .orElse(Collections.emptyList()));
            } else {
                order.setAdditionalCharge(Collections.emptyList());
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

            //set originalMoneyList;
            batchBo.getAllOriginalMoneyList().add(originalOrder);
        }
    }

    @Override
    public void postExecuteSettlement(final BatchAccountSettlementBo batchBo) {

        this.processOriginalOrderBeCreditDate(batchBo);

        this.processChargeBackOrder(batchBo);
    }

    private void processOriginalOrderBeCreditDate(final BatchAccountSettlementBo batchBo) {
        if (CollectionUtils.isNotEmpty(batchBo.getAllOriginalMoneyList())) {
            transactionMoneyService.batchSave(batchBo.getAllOriginalMoneyList());
        }
    }

    private void processChargeBackOrder(final BatchAccountSettlementBo batchBo) {
        log.info("processChargeBackOrder accountId {} size {}",
                batchBo.getAccountId(),
                Optional.ofNullable(batchBo.getTransactionMoneyList()).orElse(Lists.newArrayList())
                        .size());

        final LocalDateTime timeNow = LocalDateTimeUtil.nowUtc();
        final List<TransactionChargeBackOrder> saveList = Lists.newArrayList();
        for (final TransactionMoney order : batchBo.getTransactionMoneyList()) {
            final TransactionChargeBackOrder entity = TransactionChargeBackOrder.builder()
                    .transactionId(order.getTransactionId())
                    .uniqueId(order.getUniqueId())
                    .merchantId(order.getMerchantId())
                    .accountId(order.getAccountId())
                    .transactionTypeCode(order.getTransactionTypeCode())
                    .productCode(order.getProductCode())
                    .disputeAmount(order.getAmount())
                    .currency(order.getCurrency())
                    .disputeTime(order.getTransactionTime())
                    .createdTime(timeNow)
                    .updatedTime(timeNow)
                    .status(TransactionChargeBackStatusEnum.CHARGE_BACK)
                    .daysLeftToDefend(chargeBackOrderProperties.getDefaultDaysLeftToDefend())
                    .defenseDeadline(timeNow.plusDays(
                            chargeBackOrderProperties.getDefaultDaysLeftToDefend()))
                    .build();

            if (StringUtils.isNotBlank(order.getExtendData().getCardNumberLast4())
                    && StringUtils.isNotBlank(
                    order.getExtendData().getCardNumberFirst6())) {
                entity.setCardNumber(order.getExtendData().getCardNumberFirst6()
                        .concat("******")
                        .concat(order.getExtendData().getCardNumberLast4()));
            }

            final TransactionMoney originalOrder = batchBo.getAllOriginalMoneyList().stream()
                    .filter(x -> x.getTransactionId().equals(order.getTransactionId()))
                    .findFirst().orElse(null);

            if (Objects.nonNull(originalOrder)) {
                entity.setPaymentTime(originalOrder.getTransactionTime());
                entity.setPaymentAmount(originalOrder.getAmount());
            }

            saveList.add(entity);
        }

        transactionChargeBackOrderService.save(saveList);
    }
}
