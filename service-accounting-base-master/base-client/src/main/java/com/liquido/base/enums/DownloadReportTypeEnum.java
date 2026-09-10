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
public enum DownloadReportTypeEnum {

    ACCOUNT("ACCOUNT", "account"),

    TRANSACTION("TRANSACTION", "transaction"),

    PAYMENT_LINK("PAYMENT_LINK", "payment_link"),

    SHOPIFY("SHOPIFY", "shopify"),

    SHOPLAZZA("SHOPLAZZA", "shoplazza"),

    TRANSACTION_PAY_IN("TRANSACTION_PAY_IN", "transaction_pay_in"),

    TRANSACTION_PAY_OUT("TRANSACTION_PAY_OUT", "transaction_pay_out"),

    FINANCE("FINANCE", "finance"),

    SUPPORT_CASE("SUPPORT_CASE", "support_case"),

    ;

    private final String code;
    private final String remark;

    public static DownloadReportTypeEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(DownloadReportTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<DownloadReportTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final DownloadReportTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("downloadReportTypeEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public DownloadReportTypeEnum convertToEntityAttribute(final String dbValue) {
            return DownloadReportTypeEnum.parse(dbValue);
        }
    }
}
