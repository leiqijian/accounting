package com.liquido.worker.enums;

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
public enum CalculationTaskStateEnum {

    WAITING("WAITING", "WAITING"),
    PROCESSING("PROCESSING", "PROCESSING"),
    SUCCESS("SUCCESS", "SUCCESS"),
    FAILED("FAILED", "FAILED"),

    ;

    private final String code;

    private final String remark;

    public static CalculationTaskStateEnum parse(final String code) {
        return Arrays.stream(CalculationTaskStateEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<CalculationTaskStateEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CalculationTaskStateEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CalculationTaskState");
            }
            return enumValue.getCode();
        }

        @Override
        public CalculationTaskStateEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return CalculationTaskStateEnum.parse(dbValue);
        }
    }

}

