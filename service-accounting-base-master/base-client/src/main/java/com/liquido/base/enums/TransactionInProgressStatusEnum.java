package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionInProgressStatusEnum {

    IN_PROGRESS("IN_PROGRESS", "in progress"),

    COMPLETED("COMPLETED", "completed"),
    ;

    private final String code;
    private final String remark;

    public static TransactionInProgressStatusEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(TransactionInProgressStatusEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<TransactionInProgressStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TransactionInProgressStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception(
                        "TransactionProgressStatusEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public TransactionInProgressStatusEnum convertToEntityAttribute(final String dbValue) {
            return TransactionInProgressStatusEnum.parse(dbValue);
        }
    }
}
