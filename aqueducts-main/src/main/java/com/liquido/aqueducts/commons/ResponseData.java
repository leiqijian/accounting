package com.liquido.aqueducts.commons;

import com.liquido.aqueducts.commons.enums.ResultCode;

public class ResponseData<T> {

    private int code = 0;

    private String message = "success";

    private T data;

    public ResponseData() {}

    public ResponseData(final T data) {
        this.data = data;
    }

    public ResponseData(final T data, final int code, final String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseData(final T data, final String message) {
        this.message = message;
        this.data = data;
    }

    public ResponseData(final ResultCode resultCode) {
        this.message = resultCode.getMessage();
        this.code = resultCode.getCode();
    }

    public ResponseData(final ResultCode resultCode, T data) {
        this.message = resultCode.getMessage();
        this.code = resultCode.getCode();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }
}
