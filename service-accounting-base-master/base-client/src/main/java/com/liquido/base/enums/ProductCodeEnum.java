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
public enum ProductCodeEnum {

    SPEI("SPEI", "SPEI", "spei"),

    SPEI_VA("SPEI_VA", "SPEI VA", "spei va"),

    SPEI_BANK_TRANSFER("SPEI_BANK_TRANSFER", "SPEI(BANK TRANSFER)", "spei bank transfer"),

    TED("TED", "TED", "ted"),

    PIX("PIX", "PIX", "pix"),

    PIX_QR_CODE("PIX_QR_CODE", "PIX QR CODE", "PIX QR CODE"),

    // CREDIT_CARD("CREDIT_CARD", "Credit Card", "credit_card"),

    CARD("CARD", "CARD", "CARD"),

    BOLETO("BOLETO", "Boleto", "boleto"),

    OXXO("OXXO", "OXXO", "oxxo"),

    GIFTCARD("GIFTCARD", "Gift Card", "gift_card"),

    TOPUP("TOPUP", "Top Up", "topup"),

    UTILITY("UTILITY", "Utility", "utility"),

    // DEBIT_CARD("DEBIT_CARD", "Debit Card", "debit_card"),

    AME_QR("AME_QR", "AME QR", "ame qr"),

    MERCADO_PAGO("MERCADO_PAGO", "Mercado Pago", "mercado pago"),

    PAYPAL("PAYPAL", "PAYPAL", "paypal"),

    PIC_PAY("PIC_PAY", "Pic Pay", "pic pay"),

    PAY_CASH("PAY_CASH", "Cash", "pay cash"),

    EFECTY("EFECTY", "EFECTY", "Efecty"),

    PSE("PSE", "PSE", "pse"),

    BANCOLOMBIA_BUTTON("BANCOLOMBIA_BUTTON", "Bancolombia Button", "one of the co bank transfer"),

    BANCOLOMBIA_COLLECTION("BANCOLOMBIA_COLLECTION", "Bancolombia collection",
            "Bancolombia collection"),

    NEQUI("NEQUI", "NEQUI", "nequi"),

    WEB_PAY("WEB_PAY", "Web pay", "web pay"),

    KHIPU("KHIPU", "Khipu", "khipu"),

    MACH("MACH", "Mach", "mach"),

    CHEK("CHEK", "Chek", "chek"),

    MULTICAJA_TRANSFER("MULTICAJA_TRANSFER", "Multicaja transfer", "multicaja transfer"),

    MULTICAJA_CASH("MULTICAJA_CASH", "Multicaja cash", "multicaja cash"),

    PAGO46("PAGO46", "Pago46", "pago46"),

    HITES("HITES", "Hites", "hites"),

    FPAY("FPAY", "Fpay", "Fpay"),

    ONEPAY("ONEPAY", "OnePay", "OnePay"),

    SERVIPAG("SERVIPAG", "Servipag", "servipag"),

    SENCILLITO("SENCILLITO", "Sencillito", "sencillito"),

    SAQUE("SAQUE", "Saque", "saque"),

    // Internal definition product eg. cdi-profit
    EXTRA_ITEM("EXTRA_ITEM", "Extra Item", "extra Item"),

    PAYVALIDA("PAYVALIDA", "PayValida", "payvalida"),

    YP("YP", "Yp", "yp"),

    BCP("BCP", "Bcp", "bcp"),

    PAGO_EFECTIVO("PAGO_EFECTIVO", "Pago Efectivo", "pago efectivo"),

    SERVIFACIL("SERVIFACIL", "Servifacil", "servifacil"),

    FINTOC("FINTOC", "Fintoc", "fintoc"),

    WALLET("WALLET", "Wallet", "wallet"),

    BANK_TRANSFER("BANK_TRANSFER", "Bank Transfer", "bank transfer"),

    US_BANK_TRANSFER("US_BANK_TRANSFER", "Us Bank Transfer", "us bank transfer"),

    CO_BANK_TRANSFER("CO_BANK_TRANSFER", "CO Bank Transfer", "co bank transfer"),

    CL_BANK_TRANSFER("CL_BANK_TRANSFER", "CL Bank Transfer", "cl bank transfer"),

    PE_BANK_TRANSFER("PE_BANK_TRANSFER", "Pe Bank Transfer", "pe bank transfer"),

    ZA_BANK_TRANSFER("ZA_BANK_TRANSFER", "Za Bank Transfer", "za bank transfer"),

    AR_BANK_TRANSFER("AR_BANK_TRANSFER", "Ar Bank Transfer", "ar bank transfer"),

    BO_BANK_TRANSFER("BO_BANK_TRANSFER", "Bo Bank Transfer", "bo bank transfer"),

    CR_BANK_TRANSFER("CR_BANK_TRANSFER", "Cr Bank Transfer", "cr bank transfer"),

    DO_BANK_TRANSFER("DO_BANK_TRANSFER", "Do Bank Transfer", "do bank transfer"),

    SV_BANK_TRANSFER("SV_BANK_TRANSFER", "Sv Bank Transfer", "sv bank transfer"),

    EC_BANK_TRANSFER("EC_BANK_TRANSFER", "Ec Bank Transfer", "ec bank transfer"),

    GT_BANK_TRANSFER("GT_BANK_TRANSFER", "Gt Bank Transfer", "gt bank transfer"),

    HN_BANK_TRANSFER("HN_BANK_TRANSFER", "Hn Bank Transfer", "hn bank transfer"),

    NI_BANK_TRANSFER("NI_BANK_TRANSFER", "Ni Bank Transfer", "ni bank transfer"),

    PA_BANK_TRANSFER("PA_BANK_TRANSFER", "Pa Bank Transfer", "pa bank transfer"),

    PY_BANK_TRANSFER("PY_BANK_TRANSFER", "Py Bank Transfer", "py bank transfer"),

    UY_BANK_TRANSFER("UY_BANK_TRANSFER", "Uy Bank Transfer", "uy bank transfer"),

    ;

    private final String code;
    private final String display;
    private final String remark;

    public static ProductCodeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(ProductCodeEnum.values()).filter(tmp -> tmp.getCode().equals(code))
                .findFirst().orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<ProductCodeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final ProductCodeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("ProductCode");
            }
            return enumValue.getCode();
        }

        @Override
        public ProductCodeEnum convertToEntityAttribute(final String dbValue) {
            return ProductCodeEnum.parse(dbValue);
        }
    }

}
