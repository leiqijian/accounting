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
public enum AmountTypeEnum {

    CAPITAL("CAPITAL", "capital"),

    FEE("FEE", "fee"),

    ;

    private final String code;
    private final String remark;

    public static AmountTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(AmountTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<AmountTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final AmountTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("AmountType");
            }
            return enumValue.getCode();
        }

        @Override
        public AmountTypeEnum convertToEntityAttribute(final String dbValue) {
            return AmountTypeEnum.parse(dbValue);
        }
    }

}
