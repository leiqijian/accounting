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
 * GlobalTargetType: SELF, SUB
 */
@Getter
@RequiredArgsConstructor
public enum GlobalTargetTypeEnum {

    SELF("SELF", "self-account"),

    SUB("SUB", "sub-account"),

    ;

    private final String code;
    private final String remark;

    public static GlobalTargetTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(GlobalTargetTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<GlobalTargetTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final GlobalTargetTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("ChargeOn");
            }
            return enumValue.getCode();
        }

        @Override
        public GlobalTargetTypeEnum convertToEntityAttribute(final String dbValue) {
            return GlobalTargetTypeEnum.parse(dbValue);
        }
    }

}
