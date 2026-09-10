package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

import com.liquido.base.enums.OperationMethodEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.enums.PaymentConfigStatusEnum;
import com.liquido.core.common.convert.AesEncryptorConverter;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfigBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long accountId;

    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

    private Integer priority;

    @Convert(converter = OperationMethodEnum.Convert.class)
    private OperationMethodEnum operationMethod;

    @SensitiveField(SensitiveType.SHIELD)
    @Convert(converter = AesEncryptorConverter.class)
    private String apiKey;

    private BigDecimal maxAmount;

    private Integer delayExecution;

    private ObjectNode jsonParams;

    /**
     * status 0: disable, 1:enable
     */
    @Convert(converter = PaymentConfigStatusEnum.Convert.class)
    private PaymentConfigStatusEnum status;

    /**
     * switch false: off, true:on
     */
    private Boolean mockSwitch;

    public static class Converter implements AttributeConverter<PaymentConfigBo, String> {
        @SneakyThrows
        @Override
        public String convertToDatabaseColumn(final PaymentConfigBo data) {
            return Objects.isNull(data) ? null : JsonUtil.toJson(data);
        }

        @SneakyThrows
        @Override
        public PaymentConfigBo convertToEntityAttribute(final String dbValue) {
            return StringUtils.isBlank(dbValue)
                    ? null : JsonUtil.toBean(dbValue, PaymentConfigBo.class);
        }
    }
}
