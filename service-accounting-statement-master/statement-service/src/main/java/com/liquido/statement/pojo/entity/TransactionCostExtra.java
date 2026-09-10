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

/**
 * extra cost
 */
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_cost_extra")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class TransactionCostExtra implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "bill_date")
    private LocalDate billDate;

    @Column(name = "business_tag")
    private String businessTag;

    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "tax")
    private BigDecimal tax;

    @Column(name = "fx")
    private BigDecimal fx;

    @Column(name = "extra_fee")
    private BigDecimal extraFee;

    @Column(name = "extra_tax")
    private BigDecimal extraTax;

    @Column(name = "extra_fx")
    private BigDecimal extraFx;

    @Column(name = "exchange_fee")
    private BigDecimal exchangeFee;

    @Column(name = "adjustment_fee")
    private BigDecimal adjustmentFee;

    @Column(name = "cdi_profit_income")
    private BigDecimal cdiProfitIncome;

    @Column(name = "cost_fee")
    private BigDecimal costFee;

    @Column(name = "cost_tax")
    private BigDecimal costTax;

    @Column(name = "cost_fx")
    private BigDecimal costFx;

    @Column(name = "pricing_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum pricingCurrency;

    @Column(name = "settlement_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "version")
    private Integer version;

    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;
}
