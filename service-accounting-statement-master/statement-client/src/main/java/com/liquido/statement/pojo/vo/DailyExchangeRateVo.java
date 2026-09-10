package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class DailyExchangeRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotNull
    private Long accountId;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

    /**
     * Format: yyyy-MM-dd HH:mm:ss
     * Frequency: per hour
     */
    @NotNull
    private LocalDateTime exchangeTime;

    @NotNull
    private BigDecimal exchangeRate;

    @NotNull
    private BigDecimal ratioLose;

    @NotNull
    private BigDecimal merchantRate;
}
