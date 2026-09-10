package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * realtime transaction summary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long accountId;

    private LocalDate transactionDate;

    /**
     * daily total transaction amount,
     * unit: cent
     */
    private BigDecimal transactionAmount;

    /**
     * sum((abs)settled transaction amount) - sum((abs)other status transaction amount)
     */
    private BigDecimal transactionVolumeAmount;

    /**
     * daily total transaction count
     */
    private Integer transactionCount;

    /**
     * original transaction currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    /**
     * daily total settlement amount(excluded fee), formula: transaction_amount * exchange_rate
     * unit: cent
     */
    private BigDecimal settlementAmount;

    /**
     * sum((abs)settled settlement amount) - sum((abs)other status settlement amount)
     */
    private BigDecimal settlementVolumeAmount;

    /**
     * settlement amount converted into USD, unit: cent (fees and taxes non-deduct)
     */
    private BigDecimal settlementAmountUsd;

    /**
     * settlement volume amount converted into USD, unit: cent
     */
    private BigDecimal settlementVolumeAmountUsd;

    /**
     * daily total settlement fee amount
     * unit: cent
     */
    private BigDecimal feeAmount;

    /**
     * daily total settlement tax amount
     * unit: cent
     */
    private BigDecimal taxAmount;

    /**
     * settlement currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    private String remark;
}
