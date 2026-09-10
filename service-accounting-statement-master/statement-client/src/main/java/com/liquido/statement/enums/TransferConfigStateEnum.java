package com.liquido.statement.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferConfigStateEnum {

    ENABLE(1, "ENABLE"),
    DISABLE(0, "DISABLE"),
    ;

    private final Integer state;
    private final String remark;

    public static TransferConfigStateEnum parse(final Integer state) {
        return Arrays.stream(TransferConfigStateEnum.values())
                .filter(tmp -> tmp.getState().equals(state))
                .findFirst().orElse(DISABLE);
    }

    @Converter
    public static class Convert implements AttributeConverter<TransferConfigStateEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final TransferConfigStateEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return TransferConfigStateEnum.DISABLE.getState();
            }
            return enumValue.getState();
        }

        @Override
        public TransferConfigStateEnum convertToEntityAttribute(final Integer dbValue) {
            return TransferConfigStateEnum.parse(dbValue);
        }
    }
}
