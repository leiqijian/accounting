package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class CountryDailyBillStatisticsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate billDate;

    /**
     * transaction count
     */
    private Long transactionCount;

    /**
     * transaction amount
     * unit: USD
     */
    private BigDecimal transactionAmountUsd;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

}
