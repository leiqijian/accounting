package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum ReportTemplateEnum {

    /* default standard strategy; */
    DEFAULT("DEFAULT", "Default"),

    /* Customized strategy; */
    CUSTOMIZED("CUSTOMIZED", "Customized");

    private final String code;
    private final String remark;

    public static ReportTemplateEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(ReportTemplateEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(DEFAULT);
    }

    @Converter
    public static class Convert implements AttributeConverter<ReportTemplateEnum, String> {

        @Override
        public String convertToDatabaseColumn(final ReportTemplateEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return DEFAULT.getCode();
            }
            return enumValue.getCode();
        }

        @Override
        public ReportTemplateEnum convertToEntityAttribute(final String dbValue) {
            return ReportTemplateEnum.parse(dbValue);
        }
    }
}
