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
public enum ExtraFeeGroupEnum {
    TRANSACTION_FEE("TRANSACTION_FEE", "TransactionFee"),

    EXTRA_FEE("EXTRA_FEE", "ExtraTransactionFee"),

    TAX("TAX", "Tax"),

    EXTRA_TAX("EXTRA_TAX", "ExtraTax"),

    FX("FX", "Fx"),

    EXTRA_FX("EXTRA_FX", "ExtraFx"),

    FX_LOSE("FX_LOSE", "FX_LOSE"),
    ;

    private final String code;
    private final String groupName;

    public static ExtraFeeGroupEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(ExtraFeeGroupEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<ExtraFeeGroupEnum, String> {

        @Override
        public String convertToDatabaseColumn(final ExtraFeeGroupEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("ExtraFeeGroup");
            }
            return enumValue.getCode();
        }

        @Override
        public ExtraFeeGroupEnum convertToEntityAttribute(final String dbValue) {
            return ExtraFeeGroupEnum.parse(dbValue);
        }
    }
}
