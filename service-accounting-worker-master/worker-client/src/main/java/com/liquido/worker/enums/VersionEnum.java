package com.liquido.worker.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VersionEnum {

    NORMAL(0, "normal"),
    LOCKED(1, "locked"),

    ;

    private final Integer code;

    private final String remark;

    public static VersionEnum parse(final Integer code) {
        return Arrays.stream(VersionEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<VersionEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final VersionEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("Version");
            }
            return enumValue.getCode();
        }

        @Override
        public VersionEnum convertToEntityAttribute(final Integer dbValue) {
            if (!List.of(0, 1).contains(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return VersionEnum.parse(dbValue);
        }
    }
}
