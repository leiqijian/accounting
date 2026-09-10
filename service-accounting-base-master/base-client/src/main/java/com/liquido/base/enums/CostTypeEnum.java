package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * https://www.bindb.com/card-schemes
 * https://docs.dlocal.com/api-documentation/payins-api-reference/payment-methods/brazil
 */
@Getter
@RequiredArgsConstructor
public enum CostTypeEnum {

    APM("APM", "APM config"),

    CARD("CARD", "Card config"),

    EXTRA_INCOME("EXTRA_INCOME", "Extra income"),

    ;

    private final String code;
    private final String remark;

    public static CostTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        return Arrays.stream(CostTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code.trim())).findFirst().orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<CostTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CostTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CostTypeEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public CostTypeEnum convertToEntityAttribute(final String dbValue) {
            return CostTypeEnum.parse(dbValue);
        }
    }
}
