package com.liquido.worker.pojo.entity;

import java.io.Serializable;
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
import com.liquido.base.enums.DataSyncRefundStatusEnum;
import com.liquido.base.enums.DataSyncStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.worker.enums.VersionEnum;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

@SuppressWarnings("PMD.TooManyFields")
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_link")
@Where(clause = "del_flag = false")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class PaymentLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * payment link unique id
     */
    @Column(name = "link_id", unique = true, nullable = false)
    private String linkId;

    /**
     * merchant's order id
     */
    @Column(name = "merchant_reference", nullable = false)
    private String merchantReference;

    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "transaction_type_code", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Column(name = "product_code", nullable = false)
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * save integer type, unit：cent
     */
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = DataSyncStatusEnum.Convert.class)
    @Column(name = "payment_status", nullable = false)
    private DataSyncStatusEnum paymentStatus;

    @Column(name = "settled_unique_id")
    private String settledUniqueId;

    /**
     * timestamp UTC+0
     */
    @Column(name = "submit_time", nullable = false)
    private LocalDateTime submitTime;

    /**
     * timestamp UTC+0
     */
    @Column(name = "submit_timestamp", nullable = false)
    private Long submitTimestamp;

    /**
     * timestamp UTC+0
     */
    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    /**
     * timestamp UTC+0
     */
    @Column(name = "transaction_timestamp", nullable = false)
    private Long transactionTimestamp;

    @Convert(converter = DataSyncRefundStatusEnum.Convert.class)
    @Column(name = "refund_status")
    private DataSyncRefundStatusEnum refundStatus;

    /**
     * save integer type, unit：cent
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "refunded_unique_id")
    private String refundedUniqueId;

    /**
     * timestamp UTC+0
     */
    @Column(name = "refund_time", nullable = false)
    private LocalDateTime refundTime;

    /**
     * timestamp UTC+0
     */
    @Column(name = "refund_timestamp", nullable = false)
    private Long refundTimestamp;

    /**
     * timestamp UTC+0
     */
    @Column(name = "event_timestamp", nullable = false)
    private Long eventTimestamp;

    @SensitiveField(SensitiveType.EMAIL)
    @Column(name = "user_email")
    private String userEmail;

    @SensitiveField(SensitiveType.MOBILE)
    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "appendix")
    @Type(type = "json")
    private JsonNode appendix;

    @Column(name = "description")
    private String description;

    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "others")
    @Type(type = "json")
    private ObjectNode others;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * optimistic locking : 0-normal,1-locked
     */
    private VersionEnum version;

    /**
     * 0-normal,1-delete
     */
    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;

}
