package com.liquido.base.enums;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AmountPonEnum {
    POSITIVE(BigDecimal.ONE, "POSITIVE"),
    NEGATIVE(new BigDecimal("-1"), "NEGATIVE"),
    ;

    private final BigDecimal code;
    private final String remark;

    public static AmountPonEnum parse(final BigDecimal code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(AmountPonEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<AmountPonEnum, BigDecimal> {

        @Override
        public BigDecimal convertToDatabaseColumn(final AmountPonEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("AmountPon");
            }
            return enumValue.getCode();
        }

        @Override
        public AmountPonEnum convertToEntityAttribute(final BigDecimal dbValue) {
            return AmountPonEnum.parse(dbValue);
        }
    }

}
