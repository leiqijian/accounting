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
 * DAY/WEEK/MONTH
 */
@Getter
@RequiredArgsConstructor
public enum TimeIntervalEnum {

    DAY("DAY", "day"),

    WEEK("WEEK", "week"),

    MONTH("MONTH", "month"),

    ;

    private final String code;
    private final String remark;

    public static TimeIntervalEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(TimeIntervalEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<TimeIntervalEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TimeIntervalEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("TimeInterval");
            }
            return enumValue.getCode();
        }

        @Override
        public TimeIntervalEnum convertToEntityAttribute(final String dbValue) {
            return TimeIntervalEnum.parse(dbValue);
        }
    }

}
