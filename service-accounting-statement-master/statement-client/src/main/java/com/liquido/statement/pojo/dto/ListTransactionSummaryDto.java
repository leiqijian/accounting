package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListTransactionSummaryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

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
    private CurrencyEnum settlementCurrency;

    /**
     * version optimistic locking
     */
    private Integer version;

    private String remark;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
