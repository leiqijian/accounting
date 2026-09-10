package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class AccountBalanceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * account id
     */
    private Long accountId;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

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

    /**
     * latest bill date
     */
    private LocalDate latestBillDate;

    /**
     * todayDate
     */
    private LocalDate todayDate;

    /**
     * UTC time of the current balance
     */
    private LocalDateTime utcTime;

    private PendBusiness pendBusiness;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendBusiness implements Serializable {

        private static final long serialVersionUID = 1L;
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

        /**
         * pay_in refunding, pay_out in_progress
         */
        private BigDecimal inProgressAmount;

    }
}
