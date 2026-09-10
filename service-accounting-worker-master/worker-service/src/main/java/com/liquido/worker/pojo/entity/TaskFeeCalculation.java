package com.liquido.worker.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionDataSourceEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.enums.CalculationTaskTypeEnum;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Table(name = "task_fee_calculation")
@Where(clause = "del_flag = false")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class TaskFeeCalculation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * Payment Platform Unique ID
     */
    @Column(name = "unique_id", unique = true, nullable = false)
    private String uniqueId;

    /**
     * just for Brazil user certificate No, e.g: CPF, CNPJ
     */
    @SensitiveField(SensitiveType.SHIELD)
    @Column(name = "document_id", nullable = false)
    private String documentId;

    /**
     * UNREPEATABLE/REPEATABLE
     */
    @Column(name = "task_type", nullable = false)
    @Convert(converter = CalculationTaskTypeEnum.Convert.class)
    private CalculationTaskTypeEnum taskType;

    /**
     * WAITING/PROCESSING/SUCCESS/FAILED
     */
    @Column(name = "task_status", nullable = false)
    @Convert(converter = CalculationTaskStateEnum.Convert.class)
    private CalculationTaskStateEnum taskStatus;

    /**
     * holdStatus false:OFF, true:ON
     */
    @Column(name = "hold_status", nullable = false)
    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    @Column(name = "merchant_name")
    private String merchantName;

    /**
     * subMerchantId
     */
    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * SPEI/TED/PIX/CREDIT_CARD/BOLETO/OXXO
     */
    @Column(name = "product_code", nullable = false)
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Column(name = "sub_product_code", nullable = false)
    private String subProductCode;

    /**
     * Merchant reference idempotent_key
     */
    @Column(name = "merchant_reference")
    private String merchantReference;

    /**
     * Payer/Payee
     */
    @SensitiveField(SensitiveType.SHIELD)
    @Column(name = "account_name")
    private String accountName;

    /**
     * save integer type, unit：cent
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * MXN/BRL/USD
     */
    @Column(nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * timestamp UTC+0
     */
    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    /**
     * timestamp UTC+0, equal transactionTime
     */
    @Column(name = "transaction_timestamp", nullable = false)
    private Long transactionTimestamp;

    /**
     * submit timestamp UTC+0
     */
    @Column(name = "submit_timestamp", nullable = false)
    private Long submitTimestamp;

    /**
     * Calculate status: transactionStatus
     */
    @Column(name = "transaction_status", nullable = false)
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    /**
     * Calculate status: directionType
     */
    @Column(name = "direction_type", nullable = false)
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    /**
     * timestamp UTC+0, equal transactionTime
     */
    @Column(name = "lifecycle_timestamp", nullable = false)
    private Long lifecycleTimestamp;

    /**
     * Shown status: lifecycleStatus
     */
    @Column(name = "lifecycle_status", nullable = false)
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum lifecycleStatus;

    /**
     * Shown status: trade system data: transfer_status/status
     */
    @Column(name = "trade_transfer_status", nullable = false)
    private String tradeTransferStatus;

    /**
     * Shown status: trade system data: transaction_type
     */
    @Column(name = "trade_transaction_type", nullable = false)
    private String tradeTransactionType;

    @Column(name = "vendor", nullable = false)
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    @Column(name = "calculation_node")
    @Type(type = "json")
    private List<CalculationNode> calculationNode;

    private String comments;

    @Column(name = "description")
    private String description;

    @Column(name = "others")
    @Type(type = "json")
    private ObjectNode others;

    @Column(name = "event_timestamp", nullable = false)
    private Long eventTimestamp;

    /**
     * timestamp of final status
     */
    @Column(name = "final_status_timestamp", nullable = false)
    private Long finalStatusTimestamp;

    @Column(name = "transaction_data_source")
    @Convert(converter = TransactionDataSourceEnum.Convert.class)
    private TransactionDataSourceEnum transactionDataSource;

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
    private Integer version;

    /**
     * 0-normal,1-delete
     */
    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalculationNode {

        @Convert(converter = DirectionTypeEnum.Convert.class)
        private DirectionTypeEnum directionType;

        private Long timeStamp;

    }

}
