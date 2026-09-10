package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TaxRuleEnum 1: INCLUDING_TAX; 2:EXCLUDING_TAX
 */
@Getter
@RequiredArgsConstructor
public enum TaxRuleEnum {

    // INCLUDING TAX 11.25%
    INCLUDING_TAX(1, "Including Tax"),

    // EXCLUDING TAX 12.68%
    EXCLUDING_TAX(2, "Excluding Tax"),
    ;

    private final Integer code;
    private final String remark;

    public static TaxRuleEnum parse(final Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(TaxRuleEnum.values()).filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst().orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<TaxRuleEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final TaxRuleEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeValueModel");
            }
            return enumValue.getCode();
        }

        @Override
        public TaxRuleEnum convertToEntityAttribute(final Integer dbValue) {
            return TaxRuleEnum.parse(dbValue);
        }
    }

}
