package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.OperationMethodEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.enums.PaymentConfigStatusEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfigDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long accountId;

    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

    private Integer priority;

    @Convert(converter = OperationMethodEnum.Convert.class)
    private OperationMethodEnum operationMethod;

    @SensitiveField(SensitiveType.SHIELD)
    private String apiKey;

    private BigDecimal maxAmount;

    @SensitiveField(SensitiveType.SHIELD)
    private ObjectNode jsonParams;

    /**
     * When OperationMethod = AUTO , delay execution payment action;
     * unit: second;
     * 0:non-delay;
     * default:delay 4 hour;
     */
    private Integer delayExecution;

    /**
     * status 0: disable, 1:enable
     */
    @Convert(converter = PaymentConfigStatusEnum.Convert.class)
    private PaymentConfigStatusEnum status;

    /**
     * switch false: off, true:on
     */
    private Boolean mockSwitch;
}
