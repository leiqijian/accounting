package com.liquido.base.enums;


import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum OwnerEnum {

    APAC("APAC", "CN"),

    SSL("SSL", "MX"),

    BR("BR", "BR"),

    US("US", "US"),

    IT("IT", "IT"),

    TBD("TBD", "TBD"),
    ;

    private final String code;

    private final String mark;

    public static OwnerEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(OwnerEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<OwnerEnum, String> {

        @Override
        public String convertToDatabaseColumn(final OwnerEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CountryCode");
            }
            return enumValue.getCode();
        }

        @Override
        public OwnerEnum convertToEntityAttribute(final String dbValue) {
            return OwnerEnum.parse(dbValue);
        }
    }

}
