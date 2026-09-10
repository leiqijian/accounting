package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

/**
 * account_daily_bill
 */

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_daily_bill")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class AccountDailyBill implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * FK
     */
    @Column(name = "account_id")
    private Long accountId;

    /**
     * FK
     */
    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code")
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * timestamp
     */
    @Column(name = "bill_timestamp")
    private LocalDateTime billTimestamp;

    /**
     * timezone
     */
    @Column(name = "timezone")
    private String timezone;

    /**
     * yyyyMM
     */
    @Column(name = "bill_month")
    private Integer billMonth;

    /**
     * yyyyMMdd
     */
    @Column(name = "bill_date")
    private LocalDate billDate;

    /**
     * topup of transactions today
     */
    @Column(name = "topup_count")
    private Long topupCount;

    @Column(name = "topup_amount")
    private BigDecimal topupAmount;

    @Column(name = "topup_fee")
    private BigDecimal topupFee;

    @Column(name = "topup_tax")
    private BigDecimal topupTax;

    /**
     * number of transactions today
     */
    @Column(name = "transaction_count")
    private Long transactionCount;

    /**
     * Original transaction order amount
     */
    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    /**
     * If the current account is not a USD account,
     * the daily transaction amount needs to be converted into USD
     */
    @Column(name = "transaction_amount_usd")
    private BigDecimal transactionAmountUsd;

    /**
     * Original transaction order currency
     */
    @Column(name = "transaction_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    /**
     * after exchange to account currency settlement amount
     */
    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    @Column(name = "additional_charge")
    private BigDecimal additionalCharge;

    /**
     * after exchange to account currency
     */
    @Column(name = "settlement_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    @Column(name = "calculate_fee")
    private BigDecimal calculateFee;

    @Column(name = "calculate_tax")
    private BigDecimal calculateTax;

    @Column(name = "transaction_fee")
    private BigDecimal transactionFee;

    @Column(name = "transaction_tax")
    private BigDecimal transactionTax;

    @Column(name = "transaction_fee_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionFeeCurrency;

    @Column(name = "calculate_fee2")
    private BigDecimal calculateFee2;

    @Column(name = "calculate_tax2")
    private BigDecimal calculateTax2;

    @Column(name = "transaction_fee2")
    private BigDecimal transactionFee2;

    @Column(name = "transaction_tax2")
    private BigDecimal transactionTax2;

    @Column(name = "transaction_fee2_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionFee2Currency;

    /**
     * transfer-out of transactions today
     */
    @Column(name = "transfer_count")
    private Long transferCount;

    @Column(name = "transfer_amount")
    private BigDecimal transferAmount;

    @Column(name = "transfer_fee")
    private BigDecimal transferFee;

    @Column(name = "transfer_tax")
    private BigDecimal transferTax;

    /**
     * refund of transactions today
     */
    @Column(name = "refund_count")
    private Long refundCount;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "refund_fee")
    private BigDecimal refundFee;

    @Column(name = "refund_tax")
    private BigDecimal refundTax;

    /**
     * adjustment of transactions today
     */
    @Column(name = "adjustment_amount")
    private BigDecimal adjustmentAmount;

    @Column(name = "adjustment_count")
    private Long adjustmentCount;


    @Column(name = "start_balance")
    private BigDecimal startBalance;

    @Column(name = "happen_amount")
    private BigDecimal happenAmount;

    @Column(name = "end_balance")
    private BigDecimal endBalance;

    /**
     * extractable balance of daily end
     */
    @Column(name = "extractable_balance")
    private BigDecimal extractableBalance;

    @Column(name = "recorded_amount")
    private BigDecimal recordedAmount;

    @Type(type = "json")
    @Column(name = "daily_extractable_info")
    private DailyExtractableInfo dailyExtractableInfo;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    //@Version
    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

    /**
     * 0-unreconciled account, 1-reconciled accounts
     */
    @Column(name = "bill_reconciliation_flag")
    private Boolean billReconciliationFlag;
}
