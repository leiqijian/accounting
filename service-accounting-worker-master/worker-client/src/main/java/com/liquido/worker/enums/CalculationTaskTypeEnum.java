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
public enum CalculationTaskTypeEnum {

    UNREPEATABLE("UNREPEATABLE", "UNREPEATABLE"),
    REPEATABLE("REPEATABLE", "REPEATABLE"),

    ;

    private final String code;

    private final String remark;

    public static CalculationTaskTypeEnum parse(final String code) {
        return Arrays.stream(CalculationTaskTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<CalculationTaskTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CalculationTaskTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CalculationTaskType");
            }
            return enumValue.getCode();
        }

        @Override
        public CalculationTaskTypeEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return CalculationTaskTypeEnum.parse(dbValue);
        }
    }

}
