package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum CurrencyEnum {

    USD("USD"),
    BRL("BRL"),
    MXN("MXN"),
    COP("COP"),
    CLP("CLP"),
    EUR("EUR"),
    PEN("PEN"),
    ZAR("ZAR"),
    ARS("ARS"),
    BOB("BOB"),
    CRC("CRC"),
    DOP("DOP"),
    GTQ("GTQ"),
    HNL("HNL"),
    NIO("NIO"),
    PAB("PAB"),
    PYG("PYG"),
    UYU("UYU"),
    SVC("SVC"),
    ECS("ECS"),

    ;

    private final String code;

    public static CurrencyEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(CurrencyEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<CurrencyEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CurrencyEnum enumValue) {
            // when non-chenFan merchant repost not have fee2 currency is null
            // so fee2 currency is null
            return Objects.isNull(enumValue) ? null : enumValue.getCode();
        }

        @Override
        public CurrencyEnum convertToEntityAttribute(final String dbValue) {
            return CurrencyEnum.parse(dbValue);
        }
    }

}
