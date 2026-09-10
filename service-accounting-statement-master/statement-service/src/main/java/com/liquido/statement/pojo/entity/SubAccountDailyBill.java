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
import com.liquido.core.common.snowflake.IdGeneratorStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sub_account_daily_bill")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class SubAccountDailyBill implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "sub_account_id")
    private Long subAccountId;

    @Column(name = "bill_month")
    private Integer billMonth;

    /**
     * yyyyMMdd
     */
    @Column(name = "bill_date")
    private LocalDate billDate;

    @Column(name = "start_balance")
    private BigDecimal startBalance;

    @Column(name = "happen_amount")
    private BigDecimal happenAmount;

    @Column(name = "end_balance")
    private BigDecimal endBalance;

    @Column(name = "end_extractable_balance")
    private BigDecimal endExtractableBalance;

    @Column(name = "payin_transaction_count")
    private Long payinTransactionCount;

    @Column(name = "payin_settlement_amount")
    private BigDecimal payinSettlementAmount;

    @Column(name = "payin_transaction_fee")
    private BigDecimal payinTransactionFee;

    @Column(name = "payin_transaction_tax")
    private BigDecimal payinTransactionTax;

    @Column(name = "payout_transaction_count")
    private Long payoutTransactionCount;

    @Column(name = "payout_settlement_amount")
    private BigDecimal payoutSettlementAmount;

    @Column(name = "payout_transaction_fee")
    private BigDecimal payoutTransactionFee;

    @Column(name = "payout_transaction_tax")
    private BigDecimal payoutTransactionTax;

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

    @Column(name = "transfer_count")
    private Long transferCount;

    @Column(name = "transfer_amount")
    private BigDecimal transferAmount;

    @Column(name = "transfer_fee")
    private BigDecimal transferFee;

    @Column(name = "transfer_tax")
    private BigDecimal transferTax;

    /**
     * topup of transactions today
     */
    @Column(name = "exchange_count")
    private Long exchangeCount;

    @Column(name = "exchange_amount")
    private BigDecimal exchangeAmount;

    @Column(name = "exchange_fee")
    private BigDecimal exchangeFee;

    @Column(name = "exchange_tax")
    private BigDecimal exchangeTax;

    @Column(name = "adjustment_amount")
    private BigDecimal adjustmentAmount;

    @Column(name = "adjustment_count")
    private Long adjustmentCount;

    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;
}
