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
public enum CardTypeEnum {

    CREDIT_CARD("Credit Card", "CREDIT_CARD", "Credit Card"),

    DEBIT_CARD("Debit Card", "DEBIT_CARD", "Debit Card"),

    ;

    private final String cardTypeName;
    private final String code;
    private final String remark;

    public static CardTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        return Arrays.stream(CardTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code.trim()))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<CardTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CardTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CardType");
            }
            return enumValue.getCode();
        }

        @Override
        public CardTypeEnum convertToEntityAttribute(final String dbValue) {
            return CardTypeEnum.parse(dbValue);
        }
    }
}
