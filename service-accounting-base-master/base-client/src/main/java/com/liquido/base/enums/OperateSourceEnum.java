package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperateSourceEnum {

    OFFLINE(0, "OFFLINE"),

    ONLINE(1, "ONLINE"),
    ;

    private final Integer code;
    private final String remark;

    public static OperateSourceEnum parse(final Integer code) {
        return Arrays.stream(OperateSourceEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst().orElse(ONLINE);
    }

    @Converter
    public static class Convert implements AttributeConverter<OperateSourceEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final OperateSourceEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return OperateSourceEnum.ONLINE.getCode();
            }
            return enumValue.getCode();
        }

        @Override
        public OperateSourceEnum convertToEntityAttribute(final Integer dbValue) {
            return OperateSourceEnum.parse(dbValue);
        }
    }
}
