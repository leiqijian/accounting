package com.liquido.statement.pojo.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDailyBillDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * FK
     */
    private Long accountId;

    /**
     * FK
     */
    private Long merchantId;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * timestamp
     */
    private LocalDateTime billTimestamp;

    /**
     * timezone
     */
    private String timezone;

    /**
     * yyyyMM
     */
    private Integer billMonth;

    /**
     * yyyyMMdd
     */
    private LocalDate billDate;

    /**
     * number of transactions today
     */
    private Long topupCount;

    /**
     * amount of the topup today
     */
    private BigDecimal topupAmount;

    private BigDecimal topupFee;

    private BigDecimal topupTax;

    /**
     * number of transactions today
     */
    private Long transactionCount;

    private BigDecimal transactionAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    private BigDecimal settlementAmount;

    private BigDecimal additionalCharge;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    private BigDecimal transactionAmountUsd;

    private BigDecimal calculateFee;
    private BigDecimal calculateTax;

    private BigDecimal transactionFee;
    private BigDecimal transactionTax;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionFeeCurrency;

    private BigDecimal calculateFee2;
    private BigDecimal calculateTax2;

    private BigDecimal transactionFee2;
    private BigDecimal transactionTax2;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionFee2Currency;

    /**
     * number of transactions today
     */
    private Long transferCount;

    private BigDecimal transferAmount;

    private BigDecimal transferFee;

    private BigDecimal transferTax;

    /**
     * number of transactions today
     */
    private Long refundCount;

    private BigDecimal refundAmount;

    private BigDecimal refundFee;

    private BigDecimal refundTax;

    /**
     * data of adjustment today
     */
    private Long adjustmentCount;
    private BigDecimal adjustmentAmount;

    private BigDecimal startBalance;

    private BigDecimal happenAmount;

    private BigDecimal endBalance;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Long createdBy;

    private Long updatedBy;

}
