package com.liquido.statement.service.settlement.handler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.service.TransactionMoneyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Merchant manually refund operation
 * <p>
 * formula: amount = amount - (tradeAmount * fx) - fee
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayInRefundHandler extends AbstractAccountSettlementHandler {
    private final TransactionMoneyService transactionMoneyService;

    @Override
    public BusinessStrategyEnum getStrategy() {
        return BusinessStrategyEnum.PAY_IN_REFUND;
    }

    @Override
    public void prepareExecuteSettlement(final BatchAccountSettlementBo batchBo) {

        for (final TransactionMoney order : batchBo.getTransactionMoneyList()) {
            if (Objects.isNull(order.getExtendData())
                    || StringUtils.isBlank(order.getExtendData().getReferenceId())) {
                log.error("The transactionOrder.extendData.referenceId is null or blank, order={}",
                        order);
                throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
            }

            // obtain reference original transaction order from db
            final TransactionMoney originalOrder =
                    transactionMoneyService.queryOriginalOrderInfo(
                            order.getAccountId(),
                            order.getExtendData().getReferenceId(),
                            DirectionTypeEnum.SETTLED);

            if (Objects.isNull(originalOrder)) {
                log.error("the original order does not exist uniqueId={}, referenceId={}",
                        order.getUniqueId(), order.getExtendData().getReferenceId());
                throw CommonExceptionCode.DATA_NOT_FOUND.exception();
            }

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

            batchBo.getAllOriginalMoneyList().add(originalOrder);
        }
    }

    /**
     * process original order
     *
     * @param batchBo batchBo
     */
    @Override
    public void postExecuteSettlement(final BatchAccountSettlementBo batchBo) {

        //Update original transaction orders (beCreditedDate, holdStatus ...)
        if (CollectionUtils.isNotEmpty(batchBo.getNeedCreditOriginalMoneyList())) {
            transactionMoneyService.batchSave(batchBo.getNeedCreditOriginalMoneyList());
        }
    }
}
