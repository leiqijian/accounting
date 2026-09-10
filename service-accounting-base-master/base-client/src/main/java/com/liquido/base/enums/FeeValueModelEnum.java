package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * fixed -0/percent -1
 */
@Getter
@RequiredArgsConstructor
public enum FeeValueModelEnum {

    FIXED(0, "Fixed"),

    PERCENTAGE(1, "Percentage"),

    ;

    private final Integer code;
    private final String remark;

    public static FeeValueModelEnum parse(final Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }

        return Arrays.stream(FeeValueModelEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<FeeValueModelEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final FeeValueModelEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeValueModel");
            }
            return enumValue.getCode();
        }

        @Override
        public FeeValueModelEnum convertToEntityAttribute(final Integer dbValue) {
            return FeeValueModelEnum.parse(dbValue);
        }
    }

}
