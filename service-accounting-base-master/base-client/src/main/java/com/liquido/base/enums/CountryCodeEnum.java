package com.liquido.base.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum CountryCodeEnum {

    US("US", "UTC-5", CurrencyEnum.USD, "United States (the)"),

    BR("BR", "UTC-3", CurrencyEnum.BRL, "Brazil"),

    MX("MX", "UTC-6", CurrencyEnum.MXN, "Mexico"),

    CO("CO", "UTC-5", CurrencyEnum.COP, "Colombia"),

    CL("CL", "UTC-4", CurrencyEnum.CLP, "Chile"),

    PE("PE", "UTC-5", CurrencyEnum.PEN, "Peru"),

    ZA("ZA", "UTC+2", CurrencyEnum.ZAR, "South Africa"),

    AR("AR", "UTC-3", CurrencyEnum.ARS, "Argentina"),

    BO("BO", "UTC-4", CurrencyEnum.BOB, "Bolivia"),

    CR("CR", "UTC-6", CurrencyEnum.CRC, "Costa Rica"),

    DO("DO", "UTC-4", CurrencyEnum.DOP, "Dominican Republic"),

    SV("SV", "UTC-6", CurrencyEnum.USD, "El Salvador"),

    EC("EC", "UTC-5", CurrencyEnum.USD, "Ecuador"),

    GT("GT", "UTC-6", CurrencyEnum.GTQ, "Guatemala"),

    HN("HN", "UTC-6", CurrencyEnum.HNL, "Honduras"),

    NI("NI", "UTC-6", CurrencyEnum.NIO, "Nicaragua"),

    PA("PA", "UTC-5", CurrencyEnum.PAB, "Panama"),

    PY("PY", "UTC-4", CurrencyEnum.PYG, "Paraguay"),

    UY("UY", "UTC-3", CurrencyEnum.UYU, "Uruguay"),

    ;

    private final String code;
    private final String timezone;
    private final CurrencyEnum currency;
    private final String countryName;

    public static CountryCodeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(CountryCodeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<CountryCodeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CountryCodeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CountryCode");
            }
            return enumValue.getCode();
        }

        @Override
        public CountryCodeEnum convertToEntityAttribute(final String dbValue) {
            return CountryCodeEnum.parse(dbValue);
        }
    }


    @Converter
    public static class ListConvert
            implements AttributeConverter<List<CountryCodeEnum>, String> {
        @Override
        public String convertToDatabaseColumn(
                final List<CountryCodeEnum> channelList) {
            return CollectionUtils.isEmpty(channelList) ? null : channelList.stream()
                    .map(CountryCodeEnum::getCode)
                    .reduce((x, y) -> String.format("%s,%s", x, y))
                    .orElse(null);
        }

        @Override
        public List<CountryCodeEnum> convertToEntityAttribute(final String dbData) {
            return StringUtils.isBlank(dbData) ? List.of() : Arrays.stream(dbData.split(","))
                    .map(x -> CountryCodeEnum.parse(x.trim()))
                    .collect(Collectors.toList());
        }
    }
}
