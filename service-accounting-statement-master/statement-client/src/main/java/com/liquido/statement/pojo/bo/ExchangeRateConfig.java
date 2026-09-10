package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.util.List;
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
public class ExchangeRateConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<DailyExchangeRate> dailyExchangeRates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyExchangeRate implements Serializable {

        private static final long serialVersionUID = 1L;

        private boolean enabled;

        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum sourceCurrency;

        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum targetCurrency;

    }

}
