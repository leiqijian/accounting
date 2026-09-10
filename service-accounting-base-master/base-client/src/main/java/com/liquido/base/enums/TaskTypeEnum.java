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
public enum TaskTypeEnum {

    UNREPEATABLE("UNREPEATABLE", "unrepeatable"),

    REPEATABLE("REPEATABLE", "repeatable"),

    ;

    private final String code;
    private final String remark;

    public static TaskTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(TaskTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<TaskTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TaskTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("TaskType");
            }
            return enumValue.getCode();
        }

        @Override
        public TaskTypeEnum convertToEntityAttribute(final String dbValue) {
            return TaskTypeEnum.parse(dbValue);
        }
    }

}
