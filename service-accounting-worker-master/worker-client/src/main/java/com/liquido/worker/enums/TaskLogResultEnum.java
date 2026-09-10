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
public enum TaskLogResultEnum {

    SUCCESS("SUCCESS"),

    FAILED("FAILED"),

    ;

    private final String code;

    public static TaskLogResultEnum parse(final String code) {
        return Arrays.stream(TaskLogResultEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<TaskLogResultEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TaskLogResultEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("TaskLogResult");
            }
            return enumValue.getCode();
        }

        @Override
        public TaskLogResultEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return TaskLogResultEnum.parse(dbValue);
        }
    }

}
