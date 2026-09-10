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
public enum AccountingScheduleStateEnum {

    PENDING(0, "PENDING"),

    ACCOUNTED(1, "ACCOUNTED"),

    HOLDING(2, "HOLDING"),

    ;

    private final Integer code;
    private final String remark;

    public static AccountingScheduleStateEnum parse(final Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }

        return Arrays.stream(AccountingScheduleStateEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<AccountingScheduleStateEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final AccountingScheduleStateEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeValueModel");
            }
            return enumValue.getCode();
        }

        @Override
        public AccountingScheduleStateEnum convertToEntityAttribute(final Integer dbValue) {
            return AccountingScheduleStateEnum.parse(dbValue);
        }
    }

}
