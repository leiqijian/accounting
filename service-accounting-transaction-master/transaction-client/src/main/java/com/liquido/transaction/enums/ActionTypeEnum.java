package com.liquido.transaction.enums;

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
public enum ActionTypeEnum {

    AUTO(0, "AUTO"),
    MANUAL(1, "MANUAL"),

    ;

    private final Integer code;
    private final String remark;

    public static ActionTypeEnum parse(final Integer code) {
        return Arrays.stream(ActionTypeEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<ActionTypeEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final ActionTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("RoleInfoState");
            }
            return enumValue.getCode();
        }

        @Override
        public ActionTypeEnum convertToEntityAttribute(final Integer dbValue) {
            if (!List.of(0, 1).contains(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return ActionTypeEnum.parse(dbValue);
        }
    }
}
