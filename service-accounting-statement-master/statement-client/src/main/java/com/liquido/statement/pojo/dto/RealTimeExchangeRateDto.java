package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeExchangeRateDto implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * exchangeRateId
     */
    private Long id;

    /**
     * merchantId
     */
    private Long merchantId;

    /**
     * accountId
     */
    private Long accountId;

    /**
     * Format: yyyy-MM-dd HH:mm:ss
     */
    private LocalDateTime exchangeTime;

    /**
     * sourceCurrency
     */
    private CurrencyEnum sourceCurrency;

    /**
     * targetCurrency
     */
    private CurrencyEnum targetCurrency;

    /**
     * RealTime Exchange rate
     */
    private BigDecimal exchangeRate;

    /**
     * exchange rate loss
     */
    private BigDecimal ratioLose;

    /**
     * Exchange rate after calculation
     * <p>
     * PAY_IN  Formula: merchantRate = standardExchangeRate / (1 - ratioLose)
     * <p>
     * PAY_OUT Formula: merchantRate = standardExchangeRate * (1 - ratioLose)
     */
    private BigDecimal merchantRate;

}
