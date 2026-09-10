package com.liquido.statement.pojo.dto;

import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryDto {

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
    private Integer transactionCount;

}
