package com.liquido.core.common.exception;

/**
 * common layer exception base class
 */
public class CommonException extends FrameworkException {
    private static final long serialVersionUID = 1L;

    public CommonException(final Integer code, final String message) {
        super(code, message);
    }

    public CommonException(final Integer code, final String message, final Object tipContent) {
        super(code, message, tipContent);
    }

    public CommonException(final Integer code, final String message, final Throwable cause) {
        super(code, message, cause);
    }

    public CommonException(final String message) {
        super(message);
    }

    public CommonException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
