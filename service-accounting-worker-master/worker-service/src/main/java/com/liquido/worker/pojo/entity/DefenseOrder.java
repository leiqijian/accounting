package com.liquido.worker.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.enums.ChargebackStatusEnum;
import com.liquido.worker.enums.DefenseStatusEnum;

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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author linkaizhi
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "defense_order")
@Where(clause = "del_flag = false")
@EntityListeners({AuditingEntityListener.class})
public class DefenseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key Id
     **/
    @Id
    @GenericGenerator(name = "snowFlakeIdGenerator",
            strategy = "com.liquido.core.common.snowflake.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * original_id
     **/
    @Column(name = "original_id", nullable = false)
    private String originalId;

    /**
     * from global transaction system uniqueId, reference task_fee_calculation.unique_id
     **/
    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    /**
     * fk
     **/
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    /**
     * account id
     **/
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     **/
    @Column(name = "transaction_type_code", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY
     **/
    @Column(name = "product_code", nullable = false)
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * Original transaction amount, unit: cent
     **/
    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;

    /**
     * transaction amount, unit: cent
     **/
    @Column(name = "dispute_amount", nullable = false)
    private BigDecimal disputeAmount;

    /**
     * transaction currency
     **/
    @Column(name = "currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * card number
     */
    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    /**
     * Original order transaction time
     */
    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    /**
     * UTC+0
     **/
    @Column(name = "dispute_time", nullable = false)
    private LocalDateTime disputeTime;

    /**
     * UTC+0
     **/
    @Column(name = "defense_time", nullable = false)
    private LocalDateTime defenseTime;

    /**
     * defend count down
     */
    @Column(name = "days_left_to_defend", nullable = false)
    private Integer daysLeftToDefend;

    /**
     * UTC+0 defense deadline
     **/
    @Column(name = "defense_deadline", nullable = false)
    private LocalDateTime defenseDeadline;

    /**
     * chargeback status
     **/
    @Column(name = "chargeback_status", nullable = false)
    @Convert(converter = ChargebackStatusEnum.Convert.class)
    private ChargebackStatusEnum chargebackStatus;

    /**
     * defense status/(chargeback、under defense、defense won、defense lost)
     **/
    @Column(name = "defense_status", nullable = false)
    @Convert(converter = DefenseStatusEnum.Convert.class)
    private DefenseStatusEnum defenseStatus;

    /**
     * reason
     **/
    @Column(name = "dispute_reason", nullable = false)
    private String disputeReason;

    /**
     * defense description
     **/
    @Column(name = "defense_description", nullable = false)
    private String defenseDescription;

    /**
     * defense appendix
     */
    @Type(type = "json")
    @Column(name = "defense_appendix_ids", nullable = false)
    private List<Long> defenseAppendixIds;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /**
     * optimistic locking
     **/
    @Column(name = "version", nullable = false)
    private Integer version;

    /**
     * 0-normal，1-delete
     **/
    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;

}
