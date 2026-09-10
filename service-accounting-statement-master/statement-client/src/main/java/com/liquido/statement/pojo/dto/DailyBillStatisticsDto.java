package com.liquido.statement.pojo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyBillStatisticsDto {

    private LocalDate billDate;

    /**
     * Amount of the transaction
     */
    private BigDecimal transactionAmountUsd;

    /**
     * currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * Number of transactions
     */
    private Long transactionCount;

}
