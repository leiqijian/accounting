package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class TransactionFeeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * fee configId
     */
    private Long feeConfigurationId;

    private String feeName;

    /**
     * FeeTypeCodeEnum
     */
    private FeeTypeCodeEnum feeTypeCode;

    /**
     * FeeGroupEnum Since for Report classification summary statistics
     */
    private FeeGroupEnum feeGroup;

    /**
     * amountPon: positive or negative; positive :+1,  negative:-1
     */
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
     * after transaction amount(USD),
     * unit: cent(keep 6 decimal places)
     */
    private BigDecimal settlementAmountUsd;

    /**
     * target transaction currency
     */
    private CurrencyEnum settlementCurrency;

    /**
     * If the account currency type does not match the fee-configured currency type
     * Mark whether instant settlement
     * true: instant settlement
     * false: non-immediate settlement
     */
    private Boolean instantFlag;

    /**
     * FIXED(0, "fixed"),
     * PERCENT(1, "percent");
     */
    private FeeValueModelEnum feeValueModel;

    /**
     * AMOUNT("AMOUNT", "amount"),
     * FEE("FEE", "fee");
     */
    private FeeOnEnum feeOn;

}
