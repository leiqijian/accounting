package com.liquido.statement.enums;

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
public enum AdjustmentRevenueRegardEnum {

    NONE("NONE", "none revenue"),

    FEE("FEE", "revenue as extra fee"),

    TAX("TAX", "revenue as extra tax"),

    FX("FX", "revenue as extra fx"),
    ;

    private final String code;
    private final String remark;

    public static AdjustmentRevenueRegardEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(AdjustmentRevenueRegardEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<AdjustmentRevenueRegardEnum, String> {

        @Override
        public String convertToDatabaseColumn(final AdjustmentRevenueRegardEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("AmountType");
            }
            return enumValue.getCode();
        }

        @Override
        public AdjustmentRevenueRegardEnum convertToEntityAttribute(final String dbValue) {
            return AdjustmentRevenueRegardEnum.parse(dbValue);
        }
    }

}
