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
public enum CardRegionTypeEnum {

    LOCAL("LOCAL", "Local Card"),

    INTERNATIONAL("INTERNATIONAL", "International Card"),
    ;

    private final String code;
    private final String remark;

    public static CardRegionTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(CardRegionTypeEnum.values()).filter(tmp -> tmp.getCode().equals(code))
                .findFirst().orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<CardRegionTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CardRegionTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CardRegionType");
            }
            return enumValue.getCode();
        }

        @Override
        public CardRegionTypeEnum convertToEntityAttribute(final String dbValue) {
            return CardRegionTypeEnum.parse(dbValue);
        }
    }
}
