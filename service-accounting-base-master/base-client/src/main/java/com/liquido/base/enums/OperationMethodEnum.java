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
public enum OperationMethodEnum {

    AUTO(0, "AUTO"),

    MANUAL(1, "MANUAL"),
    ;

    private final int code;
    private final String remark;

    public static OperationMethodEnum parse(final Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(OperationMethodEnum.values())
                .filter(tmp -> tmp.getCode() == code)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<OperationMethodEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final OperationMethodEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("OperationMethod");
            }
            return enumValue.getCode();
        }

        @Override
        public OperationMethodEnum convertToEntityAttribute(final Integer dbValue) {
            return OperationMethodEnum.parse(dbValue);
        }
    }
}
