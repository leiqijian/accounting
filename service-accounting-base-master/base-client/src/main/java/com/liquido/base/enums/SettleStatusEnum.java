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
public enum SettleStatusEnum {

    WAITING("WAITING", "WAITING"),

    PROCESSING("PROCESSING", "PROCESSING"),

    SUCCESS("SUCCESS", "SUCCESS"),

    FAILED("FAILED", "FAILED"),
    ;

    private final String code;
    private final String remark;

    public static SettleStatusEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(SettleStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<SettleStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final SettleStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("SettleStatus");
            }
            return enumValue.getCode();
        }

        @Override
        public SettleStatusEnum convertToEntityAttribute(final String dbValue) {
            return SettleStatusEnum.parse(dbValue);
        }
    }

}
