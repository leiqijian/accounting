package com.liquido.base.enums;

import java.math.BigDecimal;
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
public enum AmountUnitEnum {

    CENT("CENT", BigDecimal.ONE, "CENT"),

    YUAN("YUAN", BigDecimal.valueOf(100), "YUAN");

    private final String code;
    private final BigDecimal dividend;
    private final String remark;

    public static AmountUnitEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(AmountUnitEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<AmountUnitEnum, String> {

        @Override
        public String convertToDatabaseColumn(final AmountUnitEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("AmountUnit");
            }
            return enumValue.getCode();
        }

        @Override
        public AmountUnitEnum convertToEntityAttribute(final String dbValue) {
            return AmountUnitEnum.parse(dbValue);
        }
    }
}
