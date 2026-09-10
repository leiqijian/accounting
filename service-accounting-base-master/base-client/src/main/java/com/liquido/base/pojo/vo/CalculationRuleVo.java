package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.CardGroupTypeEnum;
import com.liquido.base.enums.CardRegionTypeEnum;
import com.liquido.base.enums.CardTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationRuleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Card Type: CREDIT_CARD/DEBIT_CARD
     */
    @Convert(converter = CardTypeEnum.Convert.class)
    private CardTypeEnum cardType;

    /**
     * Card Group Type: VISA/MASTERCARD/AMEX/UNIONPAY/JCB/DINERS_CLUB/UNKNOWN
     */
    @Convert(converter = CardGroupTypeEnum.Convert.class)
    private CardGroupTypeEnum cardGroupType;

    /**
     * Installment options: Comma-separated values from "0" to "24" or "0,1,2,3"
     */
    private String installment;

    /**
     * 3DS Validation: "True" or "False"
     */
    private Boolean requires3ds;

    /**
     * Card Usage Region: "LOCAL" or "INTERNATIONAL"
     */
    @Convert(converter = CardRegionTypeEnum.Convert.class)
    private CardRegionTypeEnum cardRegionType;

}
