package com.liquido.aqueducts.commons.enums;


public enum TransactionType {
    PAY_IN,
    PAY_OUT,
    MARKET_PLACE_ORDERS;

    public static boolean isValid(final String transactionType) {
        for (TransactionType type : values()) {
            if (type.name().equals(transactionType.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static TransactionType fromString(final String transactionType) {
        if (transactionType == null) {
            return null;
        }
        for (TransactionType type : values()) {
            if (type.name().equals(transactionType.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
}
