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
import javax.persistence.Version;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.SettleStatusEnum;
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
@Table(name = "transaction_fee")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class TransactionFee implements Serializable {
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
     * fk tack_id
     */
    @Column(name = "transaction_id")
    private Long transactionId;

    /**
     * fk tack_id
     */
    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * subMerchantId
     */
    @Column(name = "sub_merchant_id")
    private String subMerchantId;


    @Column(name = "account_id")
    private Long accountId;

    /**
     * fk
     */
    @Column(name = "fee_configuration_id")
    private Long feeConfigurationId;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code")
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductEnum
     */
    @Column(name = "product_code")
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * DirectionTypeEnum
     */
    @Column(name = "direction_type")
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Column(name = "fee_name")
    private String feeName;

    /**
     * FeeTypeEnum
     */
    @Column(name = "fee_type_code")
    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeTypeCode;

    /**
     * FeeGroupEnum Since for Report classification summary statistics
     */
    @Column(name = "fee_group")
    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    @Column(name = "amount_pon")
    private BigDecimal amountPon;

    /**
     * actuality calculate fee amount,
     * unit: cent(keep 6 decimal places)
     */
    @Column(name = "calculate_amount")
    private BigDecimal calculateAmount;

    /**
     * after fee amount, unit: cent
     */
    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    /**
     * after fee amount, unit: cent
     */
    @Column(name = "settlement_amount_usd")
    private BigDecimal settlementAmountUsd;

    /**
     * target fee currency
     */
    @Column(name = "settlement_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * StatusEnum: WAITING/PROCESSING/SUCCESS/FAILED
     */
    @Column(name = "settle_status")
    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settleStatus;

    /**
     * instant settlement flag, true: realtime settlement
     */
    @Column(name = "instant_flag")
    private Boolean instantFlag;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "settle_time")
    private LocalDateTime settleTime;

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
    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "bill_id")
    private Long billId;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

}
