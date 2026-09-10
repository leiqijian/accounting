package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
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
public class ListAccountBalanceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    /**
     * account id
     */
    private Long accountId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

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
    private BigDecimal available;


    private BigDecimal unavailable;

    /**
     * PAY_IN: pending Amount = frozenAmount + exchangeAmount - refunding
     * PAY_OUT: pending Amount = frozenAmount + exchangeAmount - inProgressAmount
     * unit:cent
     */
    private BigDecimal pendingAmount;

    /**
     * Amount to hold when the transaction triggers the specified threshold
     * unit:cent
     */
    private BigDecimal holdingAmount;


    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private LocalDateTime utcTime;

    private PendBusiness pendBusiness;

    private InProgress inProgress;

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

        private BigDecimal depositAmount;

        private BigDecimal legalHoldAmount;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InProgress implements Serializable {

        private static final long serialVersionUID = 1L;

        private BigDecimal inProgressAmount;

        private BigDecimal usdInProgressAmount;

        private BigDecimal countryCurrencyInProgressAmount;

    }
}
