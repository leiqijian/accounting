package com.liquido.aqueducts.commons.enums;

public enum ResultCode {
    SUCCESS(0, "Success"),
    INVALID_PARAMETERS(4001, "Invalid Parameters"),
    CONFLICT_PARAMETERS(4002,"Conflict Parameters"),
    UNKNOWN_ERROR(4999, "Unknown Error");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultCode fromCode(final int code) {
        ResultCode[] var1 = values();
        for (ResultCode type : var1) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
