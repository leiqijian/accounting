package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDailyBillStatisticsDto implements Serializable {
    private static final long serialVersionUID = 1L;

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
