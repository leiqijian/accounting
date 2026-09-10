package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BizFinanceTypeEnum {

    PRE_FREEZING("PRE_FREEZING", "Pre-Freezing"),

    TRANSACTION_DEAL("TRANSACTION_DEAL", "Transaction-Deal"),

    UNFREEZE("UNFREEZE", "Unfreeze"),

    ;

    private final String code;
    private final String remark;

    public static BizFinanceTypeEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(BizFinanceTypeEnum.values())
                .filter(tmp -> tmp.equals(code)).findFirst().orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<BizFinanceTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final BizFinanceTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("BizFinanceType");
            }
            return enumValue.getCode();
        }

        @Override
        public BizFinanceTypeEnum convertToEntityAttribute(final String dbValue) {
            return BizFinanceTypeEnum.parse(dbValue);
        }
    }

}
