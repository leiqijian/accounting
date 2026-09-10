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

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
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
 * account_statement
 */

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_statement")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class AccountStatement implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * fk task_id
     */
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "bill_id")
    private Long billId;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code")
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductCodeEnum
     */
    @Column(name = "product_code")
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * BusinessTypeEnum
     */
    @Column(name = "business_type")
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    /**
     * AmountPonEnum, positive or negative;
     * positive: +1 ; negative: -1
     */
    @Column(name = "amount_pon")
    @Convert(converter = AmountPonEnum.Convert.class)
    private AmountPonEnum amountPon;

    /**
     * DirectionTypeEnum
     */
    @Column(name = "direction_type")
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    @Column(name = "transaction_timestamp")
    private Long transactionTimestamp;

    @Column(name = "settle_time")
    private LocalDateTime settleTime;
    /**
     * save integer type, default to penny
     */
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Column(name = "start_balance")
    private BigDecimal startBalance;

    @Column(name = "end_balance")
    private BigDecimal endBalance;

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

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;
}
