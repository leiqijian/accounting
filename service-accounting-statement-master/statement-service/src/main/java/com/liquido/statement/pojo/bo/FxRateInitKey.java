package com.liquido.statement.pojo.bo;

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
public class FxRateInitKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long accountId;

    private LocalDateTime exchangeTime;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

}
