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

/**
 * realtime transaction summary
 */
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_summary")
@SuppressWarnings("PMD.TooManyFields")
public class TransactionSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    /**
     * daily total transaction amount,
     * unit: cent
     */
    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    /**
     * sum((abs)settled transaction amount) - sum((abs)other status transaction amount)
     */
    @Column(name = "transaction_volume_amount")
    private BigDecimal transactionVolumeAmount;

    /**
     * daily total transaction count
     */
    @Column(name = "transaction_count")
    private Integer transactionCount;

    /**
     * original transaction currency
     */
    @Column(name = "transaction_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    /**
     * daily total settlement amount(excluded fee), formula: transaction_amount * exchange_rate
     * unit: cent
     */
    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    /**
     * sum((abs)settled settlement amount) - sum((abs)other status settlement amount)
     */
    @Column(name = "settlement_volume_amount")
    private BigDecimal settlementVolumeAmount;

    /**
     * settlement amount converted into USD, unit: cent (fees and taxes non-deduct)
     */
    @Column(name = "settlement_amount_usd")
    private BigDecimal settlementAmountUsd;

    /**
     * settlement volume amount converted into USD, unit: cent
     */
    @Column(name = "settlement_volume_amount_usd")
    private BigDecimal settlementVolumeAmountUsd;

    /**
     * daily total settlement fee amount
     * unit: cent
     */
    @Column(name = "fee_amount")
    private BigDecimal feeAmount;

    /**
     * daily total settlement tax amount
     * unit: cent
     */
    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    /**
     * settlement currency
     */
    @Column(name = "settlement_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * version optimistic locking
     */
    @Column(name = "version")
    private Integer version;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

}
