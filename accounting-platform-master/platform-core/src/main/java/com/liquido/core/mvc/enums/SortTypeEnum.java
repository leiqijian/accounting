package com.liquido.core.mvc.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;


/**
 * Terminal Enum
 */
@Getter
@RequiredArgsConstructor
public enum SortTypeEnum {

    /**
     * ASC
     */
    ASC("ASC", "ASC"),

    /**
     * DESC
     */
    DESC("DESC", "DESC"),
    ;

    private final String code;
    private final String remark;

    public static SortTypeEnum parse(final String code) {
        return Arrays.stream(SortTypeEnum.values())
                .filter(tmp -> tmp.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(ASC);
    }

    @Converter
    public static class Convert implements AttributeConverter<SortTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final SortTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return ASC.getCode();
            }
            return enumValue.getCode();
        }

        @Override
        public SortTypeEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                return ASC;
            }
            return SortTypeEnum.parse(dbValue);
        }
    }
}
