package com.liquido.worker.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChargebackStatusEnum {

    OPEN("OPEN", "open"),
    REVIEW("REVIEW", "review"),
    REVERSED("REVERSED", "reversed"),
    LOSE("LOSE", "lose"),
    ;

    private final String code;
    private final String remark;

    public static ChargebackStatusEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(ChargebackStatusEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<ChargebackStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final ChargebackStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception(
                        "ChargebackStatusEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public ChargebackStatusEnum convertToEntityAttribute(final String dbValue) {
            return ChargebackStatusEnum.parse(dbValue);
        }
    }

}
