package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class TransactionFeeVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long feeConfigurationId;

    private String feeName;

    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeTypeCode;

    /**
     * FeeGroupEnum Since for Report classification summary statistics
     */
    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    /**
     * fee AmountPonEnum
     */
    @Convert(converter = AmountPonEnum.Convert.class)
    private AmountPonEnum amountPon;

    /**
     * actuality calculate amount,
     * unit: cent(keep 6 decimal places)
     */
    private BigDecimal calculateAmount;

    /**
     * after transaction amount, unit: cent
     */
    private BigDecimal settlementAmount;

    /**
     * TransactionFeeAmount converted into USD,
     * unit: cent(keep 6 decimal places)
     */
    @NotNull
    private BigDecimal settlementAmountUsd;

    /**
     * target transaction currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * If the account currency type does not match the fee-configured currency type
     * Mark whether instant settlement
     * true: instant settlement
     * false: non-immediate settlement
     */
    private Boolean instantFlag;

    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeValueModel;

    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;


    /**
     * subMerchantId
     */
    private String subMerchantId;
}
