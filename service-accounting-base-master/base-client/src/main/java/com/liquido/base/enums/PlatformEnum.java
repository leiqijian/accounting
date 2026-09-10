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
public enum PlatformEnum {

    ADMIN("ADMIN", "ADMIN"),

    DASHBOARD("DASHBOARD", "DASHBOARD"),

    ;

    private final String code;
    private final String remark;

    public static PlatformEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(PlatformEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<PlatformEnum, String> {

        @Override
        public String convertToDatabaseColumn(final PlatformEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("platformEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public PlatformEnum convertToEntityAttribute(final String dbValue) {
            return PlatformEnum.parse(dbValue);
        }
    }
}
