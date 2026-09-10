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
 * AMOUNT/FEE
 */
@Getter
@RequiredArgsConstructor
public enum FeeOnEnum {

    AMOUNT("AMOUNT", "On Amount"),

    FEE("FEE", "On Fee"),

    ;

    private final String code;
    private final String remark;

    public static FeeOnEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(FeeOnEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<FeeOnEnum, String> {

        @Override
        public String convertToDatabaseColumn(final FeeOnEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeOn");
            }
            return enumValue.getCode();
        }

        @Override
        public FeeOnEnum convertToEntityAttribute(final String dbValue) {
            return FeeOnEnum.parse(dbValue);
        }
    }

}
