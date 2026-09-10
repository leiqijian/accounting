package com.liquido.statement.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionChargeBackStatusEnum {

    CHARGE_BACK("CHARGE_BACK", "charge back"),
    UNDER_DEFENSE("UNDER_DEFENSE", "under defense"),
    DEFENSE_WON("DEFENSE_WON", "defense won"),
    DEFENSE_LOST("DEFENSE_LOST", "defense lost"),
    ;

    private final String code;
    private final String remark;

    public static TransactionChargeBackStatusEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(TransactionChargeBackStatusEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<TransactionChargeBackStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TransactionChargeBackStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception(
                        "TransactionChargeBackStatusEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public TransactionChargeBackStatusEnum convertToEntityAttribute(final String dbValue) {
            return TransactionChargeBackStatusEnum.parse(dbValue);
        }
    }

}
