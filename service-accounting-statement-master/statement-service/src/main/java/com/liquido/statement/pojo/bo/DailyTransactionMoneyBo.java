package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class DailyTransactionMoneyBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal totalTransactionAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    /**
     * totalSettlementAmount
     * unit: cent
     */
    private BigDecimal totalSettlementAmount;

    /**
     * totalAdditionalCharge
     * unit: cent
     */
    private BigDecimal totalAdditionalCharge;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * totalTransactionAmountUsd
     * If the current account is not a US dollar account,
     * the daily transaction amount needs to be converted into US dollars
     * unit: cent
     */
    private BigDecimal totalTransactionAmountUsd;

    /**
     * total transactionCount
     */
    private Long totalCount;

}
