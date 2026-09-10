package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.OperationMethodEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.enums.PaymentConfigStatusEnum;
import com.liquido.core.common.convert.AesEncryptorConverter;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;

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
@Table(name = "payment_config")
@Where(clause = "del_flag = false")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class PaymentConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * fk account_product.id
     */
    @Column(name = "account_id")
    private Long accountId;

    /**
     * payment channel
     */
    @Column(name = "payment_channel")
    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

    /**
     * The priority of different payment channels for the same account,
     * the smaller the value, the higher the priority
     */
    @Column(name = "priority")
    private Integer priority;

    /**
     * operation_method, 0:auto payment; 1:manual payment
     * default:1
     */
    @Column(name = "operation_method")
    @Convert(converter = OperationMethodEnum.Convert.class)
    private OperationMethodEnum operationMethod;

    /**
     * single maximum transaction amount
     * unit:cent
     */
    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    /**
     * account local time zone
     */
    @SensitiveField(SensitiveType.SHIELD)
    @Convert(converter = AesEncryptorConverter.class)
    @Column(name = "api_key")
    private String apiKey;


    @SensitiveField(SensitiveType.SHIELD)
    @Column(name = "json_params")
    @Type(type = "json")
    private ObjectNode jsonParams;

    /**
     * When OperationMethod = AUTO , delay execution payment action;
     * unit: second;
     * 0:non-delay;
     * default:delay 4 hour;
     */
    @Column(name = "delay_execution")
    private Integer delayExecution;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * optimistic locking
     */
    @Column(name = "version")
    private Long version;

    /**
     * status 0: disable, 1:enable
     */
    @Column(name = "status")
    @Convert(converter = PaymentConfigStatusEnum.Convert.class)
    private PaymentConfigStatusEnum status;

    /**
     * switch false: off, true:on
     */
    @Column(name = "mock_switch")
    private Boolean mockSwitch;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;

}
