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
public enum FeeShowEnum {

    SINGLE("SINGLE_DISPLAY"),

    GRADIENT("GRADIENT_DISPLAY"),
    ;

    private final String code;

    public static FeeShowEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(FeeShowEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<FeeShowEnum, String> {

        @Override
        public String convertToDatabaseColumn(final FeeShowEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeShow");
            }
            return enumValue.getCode();
        }

        @Override
        public FeeShowEnum convertToEntityAttribute(final String dbValue) {
            return FeeShowEnum.parse(dbValue);
        }
    }

}
