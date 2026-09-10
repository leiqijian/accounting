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
public enum MessageTypeEnum {

    INFO("INFO", "info"),

    WARN("WARN", "warn"),

    ERROR("ERROR", "error"),

    ;

    private final String code;
    private final String remark;

    public static MessageTypeEnum parse(final String code) {
        return Arrays.stream(MessageTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<MessageTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final MessageTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeOn");
            }
            return enumValue.getCode();
        }

        @Override
        public MessageTypeEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return MessageTypeEnum.parse(dbValue);
        }
    }

}
