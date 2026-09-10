package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
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
 * transaction_fee
 */
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_cost")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class TransactionCost implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * from global transaction system uniqueId
     * reference task_fee_calculation.unique_id
     */
    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    /**
     * reference task_fee_calculation.id
     */
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * subMerchantId
     */
    @Column(name = "sub_merchant_id")
    private String subMerchantId;


    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(name = "business_type")
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code")
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * DirectionTypeEnum
     */
    @Column(name = "direction_type")
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    /**
     * ProductEnum
     */
    @Column(name = "product_code")
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * VendorCodeEnum
     */
    @Column(name = "vendor")
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    @Column(name = "operate_source", nullable = false)
    @Convert(converter = OperateSourceEnum.Convert.class)
    private OperateSourceEnum operateSource;

    /**
     * original transaction amount, unit: cent
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * original transaction currency
     */
    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * Amount converted into USD, unit: cent (fees and taxes non-deduct)
     * unit: cent
     */
    @Column(name = "amount_usd")
    private BigDecimal amountUsd;

    /**
     * Transaction fee Amount converted into USD, Unit: cent
     * unit: cent
     */
    @Column(name = "fee_usd")
    private BigDecimal feeUsd;

    /**
     * Offline additional fee
     * Extra Transaction Fee Amount converted into USD, Unit: cent
     * unit: cent
     */
    @Column(name = "extra_fee_usd")
    private BigDecimal extraFeeUsd;

    /**
     * Tax Amount converted into USD, Unit: cent
     * unit: cent
     */
    @Column(name = "tax_usd")
    private BigDecimal taxUsd;

    /**
     * Offline additional tax fee
     * Extra Transaction Tax Amount converted into USD, Unit: cent
     * unit: cent
     */
    @Column(name = "extra_tax_usd")
    private BigDecimal extraTaxUsd;

    /**
     * fx Amount converted into USD, Unit: cent
     * unit: cent
     * fx_usd = amount_usd * fx_lose
     */
    @Column(name = "fx_usd")
    private BigDecimal fxUsd;

    /**
     * Offline additional fx fee
     * Extra Transaction FX Amount converted into USD, Unit: cent
     * unit: cent
     */
    @Column(name = "extra_fx_usd")
    private BigDecimal extraFxUsd;

    /**
     * Cost Fee, Unit: cent(USD)
     */
    @Column(name = "cost_fee")
    private BigDecimal costFee;

    /**
     * Cost Tax, Unit: cen(USD)
     */
    @Column(name = "cost_tax")
    private BigDecimal costTax;

    /**
     * Cost FX, Unit: cent(USD)
     */
    @Column(name = "cost_fx")
    private BigDecimal costFx;

    /**
     * Other cost fees besides costFee, costTax, costFx
     */
    @Column(name = "cost_other")
    private BigDecimal costOther;

    /**
     * order currency exchange to USD rate
     */
    @Column(name = "fx_rate")
    private BigDecimal fxRate;

    /**
     * Currency exchange USD lose
     */
    @Column(name = "fx_lose")
    private BigDecimal fxLose;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "transaction_timestamp")
    private Long transactionTimestamp;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;
}
