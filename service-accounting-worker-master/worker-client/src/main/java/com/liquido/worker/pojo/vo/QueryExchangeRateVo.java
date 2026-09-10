package com.liquido.worker.pojo.vo;

import java.io.Serializable;
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
public class QueryExchangeRateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * from currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    /**
     * target currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

    private LocalDateTime exchangeTime;

}
