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
public class RealTimeExchangeRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

    /**
     * exchange datetime(UTC+0),
     * format: yyyy-MM-dd HH:00:00
     */
    @NotNull
    private LocalDateTime exchangeTime;

    @NotNull
    private BigDecimal exchangeRate;

}
