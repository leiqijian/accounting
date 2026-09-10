package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentConfigStatusEnum {

    ENABLE(1, "ENABLE"),

    DISABLE(0, "DISABLE"),
    ;

    private final Integer code;
    private final String remark;

    public static PaymentConfigStatusEnum parse(final Integer code) {
        if (Objects.isNull(code)) {
            return DISABLE;
        }

        return Arrays.stream(PaymentConfigStatusEnum.values())
                .filter(tmp -> tmp.getCode() == code)
                .findFirst()
                .orElse(DISABLE);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<PaymentConfigStatusEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final PaymentConfigStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception(
                        "PaymentConfigStatusEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public PaymentConfigStatusEnum convertToEntityAttribute(final Integer dbValue) {
            return PaymentConfigStatusEnum.parse(dbValue);
        }
    }

}
