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
 * WAITING/PROCESSING/SUCCESS/FAILED
 */
@Getter
@RequiredArgsConstructor
public enum StatusEnum {

    WAITING("WAITING", "waiting"),

    PROCESSING("PROCESSING", "processing"),

    SUCCESS("SUCCESS", "success"),

    FAILED("FAILED", "failed"),

    ;

    private final String code;
    private final String remark;

    public static StatusEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(StatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<StatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final StatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("Status");
            }
            return enumValue.getCode();
        }

        @Override
        public StatusEnum convertToEntityAttribute(final String dbValue) {
            return StatusEnum.parse(dbValue);
        }
    }

}
