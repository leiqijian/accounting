package com.liquido.base.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Since for Report classification summary statistics
 */
@Getter
@RequiredArgsConstructor
public enum FeeGroupEnum {

    TRANSACTION_FEE("TRANSACTION_FEE", "TransactionFee"),

    TAX("TAX", "Tax"),

    FX("FX", "Fx"),

    FX_LOSE("FX_LOSE", "FX_LOSE"),

    ;

    private final String code;
    private final String groupName;

    public static final List<FeeGroupEnum> TAX_FX_GROUP =
            List.of(FeeGroupEnum.TAX, FeeGroupEnum.FX);

    public static FeeGroupEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return TRANSACTION_FEE;
        }

        return Arrays.stream(FeeGroupEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst().orElse(TRANSACTION_FEE);
    }

    @Converter
    public static class Convert implements AttributeConverter<FeeGroupEnum, String> {

        @Override
        public String convertToDatabaseColumn(final FeeGroupEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return TRANSACTION_FEE.getCode();
            }
            return enumValue.getCode();
        }

        @Override
        public FeeGroupEnum convertToEntityAttribute(final String dbValue) {
            return FeeGroupEnum.parse(dbValue);
        }
    }

}
