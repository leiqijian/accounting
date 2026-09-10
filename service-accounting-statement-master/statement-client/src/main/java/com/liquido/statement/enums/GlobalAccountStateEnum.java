package com.liquido.statement.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalAccountStateEnum {

    ENABLE(1, "ENABLE"),
    DISABLE(0, "DISABLE"),
    ;

    private final Integer state;
    private final String remark;

    public static GlobalAccountStateEnum parse(final Integer state) {
        return Arrays.stream(GlobalAccountStateEnum.values())
                .filter(tmp -> tmp.getState().equals(state))
                .findFirst().orElse(DISABLE);
    }

    @Converter
    public static class Convert implements AttributeConverter<GlobalAccountStateEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final GlobalAccountStateEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return GlobalAccountStateEnum.DISABLE.getState();
            }
            return enumValue.getState();
        }

        @Override
        public GlobalAccountStateEnum convertToEntityAttribute(final Integer dbValue) {
            return GlobalAccountStateEnum.parse(dbValue);
        }
    }
}
