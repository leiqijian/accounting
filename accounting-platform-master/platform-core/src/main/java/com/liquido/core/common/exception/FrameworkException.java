package com.liquido.core.common.exception;

/**
 * Framework layer exception base class
 */
public class FrameworkException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public FrameworkException() {
        super();
    }

    /**
     * @param code
     * @param message
     */
    public FrameworkException(final Integer code, final String message) {
        super(code, message);
    }

    /**
     * @param code
     * @param message
     * @param tipContent
     */
    public FrameworkException(final Integer code, final String message, final Object tipContent) {
        super(code, message, tipContent);
    }

    /**
     * @param code
     * @param message
     * @param cause
     */
    public FrameworkException(final Integer code, final String message, final Throwable cause) {
        super(code, message, cause);
    }

    /**
     * @param message
     */
    public FrameworkException(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public FrameworkException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code
     * @param message
     * @param args
     */
    public FrameworkException(final Integer code, final String message, final Object[] args) {
        super(code, message, args);
    }

    /**
     * @param code
     * @param message
     * @param cause
     * @param args
     */
    public FrameworkException(final Integer code, final String message, final Throwable cause,
                              final Object[] args) {
        super(code, message, cause, args);
    }

    /**
     * @param message
     * @param args
     */
    public FrameworkException(final String message, final Object[] args) {
        super(message, args);
    }

    /**
     * @param message
     * @param cause
     * @param args
     */
    public FrameworkException(final String message, final Throwable cause, final Object[] args) {
        super(message, cause, args);
    }

}
