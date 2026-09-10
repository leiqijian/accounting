package com.liquido.statement.pojo.vo;

import java.io.Serializable;
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
public class QueryDailyExchangeRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotNull
    private Long accountId;

    /**
     * UTC+0 timezone
     * Format: yyyy-MM-dd HH:00:00
     */
    @NotNull
    private LocalDateTime exchangeTime;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

}
