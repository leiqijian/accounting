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

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.statement.enums.PaymentTransactionStatusEnum;
import com.liquido.statement.pojo.bo.PaymentConfigBo;

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
@Table(name = "transaction_payout")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class TransactionPayout implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * from global transaction uniqueId
     */
    @Column(name = "unique_id", nullable = false)
    private Long uniqueId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "payment_config")
    @Convert(converter = PaymentConfigBo.Converter.class)
    private PaymentConfigBo paymentConfig;

    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * payment channel
     */
    @Column(name = "payment_channel")
    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

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
     * Json data
     */
    @Column(name = "target_info")
    @Type(type = "json")
    private ObjectNode targetInfo;

    @Column(name = "transaction_status")
    @Convert(converter = PaymentTransactionStatusEnum.Convert.class)
    private PaymentTransactionStatusEnum transactionStatus;


    @Column(name = "delay_execute_time")
    private LocalDateTime delayExecuteTime;

    @Column(name = "settle_time")
    private LocalDateTime settleTime;

    /**
     * Json data
     */
    @SensitiveField(SensitiveType.SHIELD)
    @Column(name = "payment_response")
    private String paymentResponse;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

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

    @Column(name = "remark")
    private String remark;
}
