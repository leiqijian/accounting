package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperateModeEnum {

    AUTO(0, "AUTO"),

    MANUAL(1, "MANUAL"),

    ;

    private final Integer code;
    private final String remark;

    public static OperateModeEnum parse(final Integer code) {
        return Arrays.stream(OperateModeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst().orElse(MANUAL);
    }

    @Converter
    public static class Convert implements AttributeConverter<OperateModeEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final OperateModeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return OperateModeEnum.MANUAL.getCode();
            }
            return enumValue.getCode();
        }

        @Override
        public OperateModeEnum convertToEntityAttribute(final Integer dbValue) {
            return OperateModeEnum.parse(dbValue);
        }
    }
}
