package com.liquido.aqueducts.commons.enums;


public enum ProductCode {
    PIX,
    TED,
    SPEI,
    SPEI_VA,
    BOLETO,
    OXXO,
    GIFTCARD,
    TOPUP,
    UTILITY,
    AME_QR,
    MERCADO_PAGO,
    PAYPAL,
    PIC_PAY,
    PAY_CASH,
    SPEI_BANK_TRANSFER,
    PSE,
    NEQUI,
    CO_BANK_TRANSFER,
    CL_BANK_TRANSFER,
    PIX_QR_CODE,
    EFECTY,
    BANCOLOMBIA_BUTTON,
    BANCOLOMBIA_COLLECTION,
    BANK_TRANSFER,

    MULTICAJA_TRANSFER,
    MULTICAJA_CASH,
    HITES,
    FPAY,
    CHEK,
    MACH,
    PAGO46,
    KHIPU,
    WEB_PAY,
    SERVIPAG,
    SENCILLITO,

    // CREDIT_CARD & DEBIT_CARD rename to CARD
    CARD,
    WALLET,
    PE_BANK_TRANSFER,
    ZA_BANK_TRANSFER,
    ;

    public static boolean isValid(final String productCode) {
        for (ProductCode code : values()) {
            if (code.name().equals(productCode.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static ProductCode fromString(final String productCode) {
        if (productCode == null) {
            return null;
        }
        for (ProductCode code : values()) {
            if (code.name().equals(productCode.toUpperCase())) {
                return code;
            }
        }
        return null;
    }

}
