package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Exchange rate from remote service
     */
    private BigDecimal exchangeRate;

}
