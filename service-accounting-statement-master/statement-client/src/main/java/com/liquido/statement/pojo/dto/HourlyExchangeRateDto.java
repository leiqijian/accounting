package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyExchangeRateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

    /**
     * exchange datetime(UTC+0),
     * format: yyyy-MM-dd HH:00:00
     */
    private LocalDateTime exchangeTime;

    private BigDecimal exchangeRate;

}
