package com.liquido.base.enums;

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
public enum HoldStatusEnum {

    NORMAL(0, "NORMAL"),

    HOLD(1, "HOLD/LOCKED"),

    UNHOLD(2, "UNHOLD/UNLOCKED"),
    ;

    private final Integer code;
    private final String remark;

    public static final List<HoldStatusEnum> NORMAL_STATUS =
            List.of(HoldStatusEnum.NORMAL, HoldStatusEnum.UNHOLD);

    public static HoldStatusEnum parse(final Integer code) {
        if (Objects.isNull(code)) {
            return NORMAL;
        }
        return Arrays.stream(HoldStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code)).findFirst().orElse(NORMAL);
    }

    @Converter
    public static class Convert implements AttributeConverter<HoldStatusEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final HoldStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("HoldStatus");
            }
            return enumValue.getCode();
        }

        @Override
        public HoldStatusEnum convertToEntityAttribute(final Integer dbValue) {
            return HoldStatusEnum.parse(dbValue);
        }
    }

}
