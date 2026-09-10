package com.liquido.aqueducts.commons.exception;

import com.liquido.aqueducts.commons.enums.ResultCode;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1234569621911702072L;

    private int code = 9999;

    public BusinessException() {}

    public BusinessException(final int code, final String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(final int code, final String message, final Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public BusinessException(final ResultCode resultCode, final Throwable throwable) {
        super(resultCode.getMessage(), throwable);
        this.code = resultCode.getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
