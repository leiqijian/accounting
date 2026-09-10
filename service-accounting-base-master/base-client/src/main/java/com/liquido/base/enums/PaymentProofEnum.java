package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum PaymentProofEnum {

    MX_SPEI_ARCUS("MX_SPEI_ARCUS", CountryCodeEnum.MX, ProductCodeEnum.SPEI,
            VendorCodeEnum.ARCUS, Collections.emptyList()),

    MX_SPEI_UNIPAGOS("MX_SPEI_UNIPAGOS", CountryCodeEnum.MX, ProductCodeEnum.SPEI,
            VendorCodeEnum.UNIPAGOS, Collections.emptyList()),

    MX_SPEI_STP("MX_SPEI_STP", CountryCodeEnum.MX, ProductCodeEnum.SPEI,
            VendorCodeEnum.STP, Collections.emptyList()),

    BR_PIX_BS2("BR_PIX_BS2", CountryCodeEnum.BR, ProductCodeEnum.PIX, VendorCodeEnum.BS2,
            Collections.emptyList()),

    BR_PIX_BEXS("BR_PIX_BEXS", CountryCodeEnum.BR, ProductCodeEnum.PIX, VendorCodeEnum.BEXS,
            Collections.emptyList()),

    BR_PIX_RENDIMENTO("BR_PIX_RENDIMENTO", CountryCodeEnum.BR, ProductCodeEnum.PIX,
            VendorCodeEnum.RENDIMENTO,
            Collections.emptyList()),

    CO_CO_BANK_TRANSFER("CO_CO_BANK_TRANSFER", CountryCodeEnum.CO,
            ProductCodeEnum.CO_BANK_TRANSFER, null, Collections.emptyList()),

    ;

    private final String code;
    private final CountryCodeEnum countryCode;
    private final ProductCodeEnum productCode;
    private final VendorCodeEnum vendorCode;
    private final List<Integer> excludeBankCode;

    public static PaymentProofEnum parse(final String code) {
        return Arrays.stream(PaymentProofEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    public static PaymentProofEnum parse(final CountryCodeEnum countryCode,
                                         final ProductCodeEnum productCode,
                                         final VendorCodeEnum vendorCode,
                                         final Integer bankCode) {
        return Arrays.stream(PaymentProofEnum.values())
                .filter(v -> {
                    if (Objects.nonNull(v.getCountryCode()) && v.getCountryCode() != countryCode) {
                        return false;
                    }
                    if (Objects.nonNull(v.getProductCode()) && v.getProductCode() != productCode) {
                        return false;
                    }
                    if (Objects.nonNull(v.getVendorCode()) && v.getVendorCode() != vendorCode) {
                        return false;
                    }
                    return !Objects.nonNull(v.getExcludeBankCode())
                            || !v.getExcludeBankCode().contains(bankCode);
                })
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<PaymentProofEnum, String> {

        @Override
        public String convertToDatabaseColumn(final PaymentProofEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("PaymentProofEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public PaymentProofEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return PaymentProofEnum.parse(dbValue);
        }
    }

}
