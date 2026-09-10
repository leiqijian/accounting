package com.liquido.core.common.exception;

/**
 * exception extension class
 */
public class ExtendException extends RuntimeException {

    private Class<?> superClass;

    private String code;

    private String msg;

    public Class<?> getSuperClass() {
        return superClass;
    }

    public void setSuperClass(final Class<?> superClass) {
        this.superClass = superClass;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }
}
