package com.liquido.statement.pojo.dto;

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
@SuppressWarnings("PMD.TooManyFields")
public class MerchantBalanceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * account real-time balance
     * latestDailyBalance + subTotalAmount
     * unit:cent
     */
    private BigDecimal totalBalance;

    /**
     * account extractable Amount
     * unit:cent
     */
    private BigDecimal extractableAmount;

    /**
     * Record the currently frozen amount in extractable balance
     * unit:cent
     */
    private BigDecimal frozenAmount;

    /**
     * Record the currently exchangeable amount in extractable balance
     * unit:cent
     */
    private BigDecimal exchangeAmount;


    private BigDecimal unavailableAmount;


    /**
     * account pending Amount
     * unit:cent
     */
    private BigDecimal pendingAmount;

    /**
     * Amount to hold when the transaction triggers the specified threshold
     * unit:cent
     */
    private BigDecimal holdingAmount;

    /**
     * Real-Time Transactions Count (at this time)
     */
    private Long realTimeTransactionsCount;

    /**
     * Real-Time Transactions Amount (at this time)
     * unit:cent
     */
    private BigDecimal realTimeTransactionsAmount;

    /**
     * Account yesterday balance
     * unit:cent
     */
    private BigDecimal yesterdayBalance;

    /**
     * Yesterday Transactions Count
     */
    private Long yesterdayTransactionsCount;

    /**
     * Yesterday Transactions Amount
     */
    private BigDecimal yesterdayTransactionsAmount;


    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

}
