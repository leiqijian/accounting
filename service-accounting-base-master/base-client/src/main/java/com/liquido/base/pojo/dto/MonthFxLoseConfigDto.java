package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("PMD.TooManyFields")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthFxLoseConfigDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountId;

    /**
     * same fee amount for each transaction-0/percentage fee rate for each transaction-1
     */
    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeTypeCode;

    /**
     * the month this configuration active for, eg. 202204
     */
    private Integer activeMonth;


    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeValueModel;

    /**
     * Since for Report classification summary statistics
     */
    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    private BigDecimal feeValue;

    /**
     * MXN/BRL/USD
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private BigDecimal minFeeAmount;

    private BigDecimal maxFeeAmount;

    /**
     * fee calculation base on:FEE-0;SETTLE_AMOUNT-1
     */
    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    /**
     * SETTLED/REFUND/CHARGE_BACK
     */
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;


}
