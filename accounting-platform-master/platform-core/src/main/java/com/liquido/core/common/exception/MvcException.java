package com.liquido.core.common.exception;

/**
 * MVC layer exception base class
 */
public class MvcException extends FrameworkException {

    private static final long serialVersionUID = 1L;

    /**
     * @param code
     * @param message
     */
    public MvcException(final Integer code, final String message) {
        super(code, message);
    }

    /**
     * @param code
     * @param message
     * @param tipContent
     */
    public MvcException(final Integer code, final String message, final Object tipContent) {
        super(code, message, tipContent);
    }

    /**
     * @param code
     * @param message
     * @param cause
     */
    public MvcException(final Integer code, final String message, final Throwable cause) {
        super(code, message, cause);
    }

    /**
     * @param message
     */
    public MvcException(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MvcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code
     * @param message
     * @param args
     */
    public MvcException(final Integer code, final String message, final Object[] args) {
        super(code, message, args);
    }

    /**
     * @param code
     * @param message
     * @param cause
     * @param args
     */
    public MvcException(final Integer code, final String message, final Throwable cause,
                        final Object[] args) {
        super(code, message, cause, args);
    }

    /**
     * @param message
     * @param args
     */
    public MvcException(final String message, final Object[] args) {
        super(message, args);
    }

    /**
     * @param message
     * @param cause
     * @param args
     */
    public MvcException(final String message, final Throwable cause, final Object[] args) {
        super(message, cause, args);
    }
}
