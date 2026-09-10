package com.liquido.statement.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MerchantDepositStateEnum {

    ENABLE(1, "ENABLE"),
    DISABLE(0, "DISABLE"),
    ;

    private final Integer state;
    private final String remark;

    public static MerchantDepositStateEnum parse(final Integer state) {
        return Arrays.stream(MerchantDepositStateEnum.values())
                .filter(tmp -> tmp.getState().equals(state))
                .findFirst().orElse(DISABLE);
    }

    @Converter
    public static class Convert implements AttributeConverter<MerchantDepositStateEnum, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final MerchantDepositStateEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return MerchantDepositStateEnum.DISABLE.getState();
            }
            return enumValue.getState();
        }

        @Override
        public MerchantDepositStateEnum convertToEntityAttribute(final Integer dbValue) {
            return MerchantDepositStateEnum.parse(dbValue);
        }
    }
}
