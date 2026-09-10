package com.liquido.worker.service.calculate;

import java.util.Objects;

import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;

import com.fasterxml.jackson.databind.JsonNode;

public class CalculateManager {

    /**
     * getCardType
     *
     * @param taskOrder
     * @return
     */
    public static CardTypeEnum getCardType(final TaskFeeCalculation taskOrder) {

        if (Objects.isNull(taskOrder) || Objects.isNull(taskOrder.getOthers())) {
            return CardTypeEnum.CREDIT_CARD;
        }

        final JsonNode cardType = taskOrder.getOthers().get("cardType");
        return Objects.isNull(cardType) ? CardTypeEnum.CREDIT_CARD
                : CardTypeEnum.parse(cardType.asText().trim());
    }

    /**
     * getCardBrand
     *
     * @param taskOrder
     * @return
     */
    public static CreditCardGroupCodeEnum getCardBrand(final TaskFeeCalculation taskOrder) {
        if (Objects.isNull(taskOrder) || Objects.isNull(taskOrder.getOthers())) {
            return CreditCardGroupCodeEnum.DEFAULT;
        }

        final JsonNode cardBrand = taskOrder.getOthers().get("cardBrand");
        return Objects.isNull(cardBrand) ? CreditCardGroupCodeEnum.DEFAULT
                : CreditCardGroupCodeEnum.parse(cardBrand.asText().trim());
    }

    /**
     * when installment > 1 is installment trading else non-installment trading
     *
     * @param taskOrder
     * @return
     */
    public static int getCardInstallments(final TaskFeeCalculation taskOrder) {

        if (Objects.isNull(taskOrder) || Objects.isNull(taskOrder.getOthers())) {
            return 0;
        }

        final JsonNode cardInstallments = taskOrder.getOthers().get("cardInstallments");
        if (Objects.isNull(cardInstallments)) {
            return 0;
        }

        return cardInstallments.asInt(0) > 1 ? cardInstallments.asInt() : 0;
    }

}
