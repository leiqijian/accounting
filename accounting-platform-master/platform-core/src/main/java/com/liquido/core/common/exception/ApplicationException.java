package com.liquido.core.common.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * top-level exception base class
 */
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * error code
     */
    private Integer code;

    /**
     * error msg parameter
     */
    private Object[] args;

    /**
     * http response content when error
     */
    private Object data;


    public ApplicationException() {
        super();
    }

    /**
     * @param message error msg
     */
    public ApplicationException(final String message) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY));
    }

    /**
     * @param message error msg
     * @param cause   Throwable
     */
    public ApplicationException(final String message, final Throwable cause) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY), cause);
    }

    /**
     * @param code    error code
     * @param message error msg
     */
    public ApplicationException(final Integer code, final String message) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY));
        this.code = code;
    }

    /**
     * @param code
     * @param message
     * @param tipContent
     */
    public ApplicationException(final Integer code, final String message, final Object tipContent) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY));
        this.code = code;
        this.data = tipContent;
    }

    /**
     * @param code    error code
     * @param message error msg
     * @param cause   Throwable
     */
    public ApplicationException(final Integer code, final String message, final Throwable cause) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY), cause);
        this.code = code;
    }

    /**
     * @param message error msg
     * @param args    args
     */
    public ApplicationException(final String message, final Object[] args) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY));
        this.setArgs(args);
    }

    /**
     * @param message error msg
     * @param cause   Throwable
     * @param args    args
     */
    public ApplicationException(final String message, final Throwable cause, final Object[] args) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY), cause);
        this.setArgs(args);
    }

    /**
     * @param code    error code
     * @param message message
     * @param args    args
     */
    public ApplicationException(final Integer code, final String message, final Object[] args) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY));
        this.code = code;
        this.setArgs(args);
    }

    /**
     * @param code    error code
     * @param message error msg
     * @param cause   Throwable
     * @param args    args
     */
    public ApplicationException(final Integer code, final String message, final Throwable cause,
                                final Object[] args) {
        super(StringUtils.defaultIfBlank(message, StringUtils.EMPTY), cause);
        this.code = code;
        this.setArgs(args);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(final Object[] args) {
        this.args = args;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }
}
