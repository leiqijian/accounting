package com.liquido.statement.pojo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.TransactionInProgressStatusEnum;
import com.liquido.base.enums.TransactionStatusEnum;
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

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_in_progress")
@Where(clause = "del_flag = false")
public class TransactionInProgress {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;
    /**
     * fk tack_id
     */
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;
    /**
     * uk_unique_id
     */
    @Column(name = "unique_id", unique = true, nullable = false)
    private String uniqueId;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "transaction_type_code", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    //pre calculate fee currency =account currency
    @Column(name = "fee", nullable = false)
    private BigDecimal fee;

    //pre calculate tax currency =account currency
    @Column(name = "tax", nullable = false)
    private BigDecimal tax;

    //pre-calculate amount netAmount=amount/rate + fee + tax, currency =account currency
    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "status", nullable = false)
    @Convert(converter = TransactionInProgressStatusEnum.Convert.class)
    private TransactionInProgressStatusEnum status;

    @Column(name = "transaction_status", nullable = false)
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    @Column(name = "direction_type", nullable = false)
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Column(name = "lifecycle_status", nullable = false)
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum lifecycleStatus;

    @Column(name = "lifecycle_timestamp", nullable = false)
    private Long lifecycleTimestamp;

    @Column(name = "vendor", nullable = false)
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * optimistic locking : 0-normal,1-locked
     */
    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal,1-delete
     */
    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;

}
