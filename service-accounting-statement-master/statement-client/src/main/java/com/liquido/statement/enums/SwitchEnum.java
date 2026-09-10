package com.liquido.statement.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SwitchEnum {

    ON("ON", "ON"),
    OFF("OFF", "OFF"),
    ;

    private final String code;
    private final String remark;

    public static SwitchEnum parse(final String code) {
        return Arrays.stream(SwitchEnum.values()).filter(tmp -> tmp.getCode().equals(code))
                .findFirst().orElse(OFF);
    }

    @Converter
    public static class Convert implements AttributeConverter<SwitchEnum, String> {

        @Override
        public String convertToDatabaseColumn(final SwitchEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return SwitchEnum.OFF.getCode();
            }
            return enumValue.getCode();
        }

        @Override
        public SwitchEnum convertToEntityAttribute(final String dbValue) {
            return SwitchEnum.parse(dbValue);
        }
    }
}
