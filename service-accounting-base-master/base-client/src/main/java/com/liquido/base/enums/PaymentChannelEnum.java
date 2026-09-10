package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentChannelEnum {

    // Brazil
    PIX("PIX", CountryCodeEnum.BR, Boolean.TRUE),
    TED("TED", CountryCodeEnum.BR, Boolean.FALSE),

    // Mexico
    OXXO("OXXO", CountryCodeEnum.MX, Boolean.FALSE),
    SPEI("SPEI", CountryCodeEnum.MX, Boolean.TRUE),

    // Colombia
    PSE("PSE", CountryCodeEnum.CO, Boolean.TRUE),
    CO_BANK_TRANSFER("CO_BANK_TRANSFER", CountryCodeEnum.CO, Boolean.FALSE),

    // Chile
    CL_BANK_TRANSFER("CL_BANK_TRANSFER", CountryCodeEnum.CL, Boolean.TRUE),

    // America
    US_BANK_TRANSFER("US_BANK_TRANSFER", CountryCodeEnum.US, Boolean.TRUE),

    // Peru
    PE_BANK_TRANSFER("PE_BANK_TRANSFER", CountryCodeEnum.PE, Boolean.TRUE),

    // South Africa
    ZA_BANK_TRANSFER("ZA_BANK_TRANSFER", CountryCodeEnum.ZA, Boolean.TRUE),

    AR_BANK_TRANSFER("AR_BANK_TRANSFER", CountryCodeEnum.AR, Boolean.TRUE),

    BO_BANK_TRANSFER("BO_BANK_TRANSFER", CountryCodeEnum.BO, Boolean.TRUE),

    CR_BANK_TRANSFER("CR_BANK_TRANSFER", CountryCodeEnum.CR, Boolean.TRUE),

    DO_BANK_TRANSFER("DO_BANK_TRANSFER", CountryCodeEnum.DO, Boolean.TRUE),

    SV_BANK_TRANSFER("SV_BANK_TRANSFER", CountryCodeEnum.SV, Boolean.TRUE),

    EC_BANK_TRANSFER("EC_BANK_TRANSFER", CountryCodeEnum.EC, Boolean.TRUE),

    GT_BANK_TRANSFER("GT_BANK_TRANSFER", CountryCodeEnum.GT, Boolean.TRUE),

    HN_BANK_TRANSFER("HN_BANK_TRANSFER", CountryCodeEnum.HN, Boolean.TRUE),

    NI_BANK_TRANSFER("NI_BANK_TRANSFER", CountryCodeEnum.NI, Boolean.TRUE),

    PA_BANK_TRANSFER("PA_BANK_TRANSFER", CountryCodeEnum.PA, Boolean.TRUE),

    PY_BANK_TRANSFER("PY_BANK_TRANSFER", CountryCodeEnum.PY, Boolean.TRUE),

    UY_BANK_TRANSFER("UY_BANK_TRANSFER", CountryCodeEnum.UY, Boolean.TRUE),

    ;


    private final String code;
    private final CountryCodeEnum countryCode;
    private final Boolean defaultChannel;

    public static PaymentChannelEnum parse(final String paymentChannelCode) {
        return Arrays.stream(PaymentChannelEnum.values())
                .filter(item -> item.getCode().equals(paymentChannelCode))
                .findFirst()
                .orElseThrow(BaseExceptionCode.PAYMENT_CHANNEL_UNDEFINED::exception);
    }

    public static PaymentChannelEnum getDefaultChannel(final CountryCodeEnum country) {
        return Arrays.stream(PaymentChannelEnum.values())
                .filter(item -> item.getCountryCode().equals(country) && item.getDefaultChannel())
                .findFirst()
                .orElseThrow(BaseExceptionCode.PAYMENT_CHANNEL_UNDEFINED::exception);
    }

    @Converter
    public static class Convert implements AttributeConverter<PaymentChannelEnum, String> {

        @Override
        public String convertToDatabaseColumn(final PaymentChannelEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("PaymentChannel");
            }
            return enumValue.getCode();
        }

        @Override
        public PaymentChannelEnum convertToEntityAttribute(final String dbValue) {
            return PaymentChannelEnum.parse(dbValue);
        }
    }
}
