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
public enum LarkRemindRankEnum {

    INFO("INFO", "BLUE", "INFO"),

    WARN("WARN", "YELLOW", "WARN"),

    ERROR("ERROR", "RED", "ERROR");

    private final String code;
    private final String color;
    private final String remark;

    public static LarkRemindRankEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(LarkRemindRankEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<LarkRemindRankEnum, String> {

        @Override
        public String convertToDatabaseColumn(final LarkRemindRankEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("AmountUnit");
            }
            return enumValue.getCode();
        }

        @Override
        public LarkRemindRankEnum convertToEntityAttribute(final String dbValue) {
            return LarkRemindRankEnum.parse(dbValue);
        }
    }
}
