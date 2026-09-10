package com.liquido.base.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * transactions amount-0/transaction counts-1
 */
@Getter
@RequiredArgsConstructor
public enum MonthlyVolumeTypeEnum {

    AMOUNT(0, "transactions amount"),

    COUNTS(1, "transaction counts"),

    ;

    private final Integer code;
    private final String remark;

    public static MonthlyVolumeTypeEnum parse(final Integer code) {
        if (Objects.isNull(code) || !List.of(0, 1).contains(code)) {
            return null;
        }

        return Arrays.stream(MonthlyVolumeTypeEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<MonthlyVolumeTypeEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final MonthlyVolumeTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeValueModel");
            }
            return enumValue.getCode();
        }

        @Override
        public MonthlyVolumeTypeEnum convertToEntityAttribute(final Integer dbValue) {
            return MonthlyVolumeTypeEnum.parse(dbValue);
        }
    }

}
