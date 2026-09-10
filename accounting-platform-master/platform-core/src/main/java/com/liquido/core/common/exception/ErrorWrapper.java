package com.liquido.core.common.exception;

import java.text.MessageFormat;
import java.util.Objects;

import com.liquido.core.mvc.dto.ResponseDto;

/**
 * error wrapped interface class
 */
public interface ErrorWrapper {

    Object[] DEFAULT_MSG_ARGS = new Object[] {"", "", "", "", "", "", "", "", "", ""};

    Integer getCode();

    String getMessage();

    default ResponseDto response() {
        return ResponseDto.newInstance(getCode(), getMessage(), null);
    }

    /**
     * Use the message defined in the enumeration as the return information
     *
     * @return
     */
    default ResponseDto parse() {
        return ResponseDto.newInstance(getCode(), getMessage(), "");
    }

    /**
     * @param args
     * @return
     */
    default ResponseDto parse(final Object... args) {
        return ResponseDto.newInstance(getCode(), MessageFormat.format(getMessage(),
                Objects.nonNull(args) && args.length > 0 ? args : DEFAULT_MSG_ARGS), "");
    }

    /**
     * @param message
     * @param args
     * @return
     */
    default ResponseDto parseMsg(final String message, final Object... args) {
        return ResponseDto.newInstance(getCode(), MessageFormat.format(message,
                Objects.nonNull(args) && args.length > 0 ? args : DEFAULT_MSG_ARGS), "");
    }

    /**
     * @return
     */
    default ApplicationException exception() {
        return new ApplicationException(getCode(), getMessage());
    }

    default ApplicationException exceptionTip(final Object tipContent) {
        return new ApplicationException(getCode(), getMessage(), tipContent);
    }

    /**
     * @param args
     * @return
     */
    default ApplicationException exception(final Object... args) {
        return new ApplicationException(getCode(), MessageFormat.format(getMessage(),
                Objects.nonNull(args) && args.length > 0 ? args : DEFAULT_MSG_ARGS));
    }

    /**
     * @param cause
     * @param args
     * @return
     */
    default ApplicationException exception(final Throwable cause, final Object... args) {
        return new ApplicationException(getCode(), MessageFormat.format(getMessage(),
                Objects.nonNull(args) && args.length > 0 ? args : DEFAULT_MSG_ARGS), cause);
    }

    /**
     * @param message
     * @param args
     * @return
     */
    default ApplicationException exceptionMsg(final String message, final Object... args) {
        return new ApplicationException(getCode(), MessageFormat.format(message,
                Objects.nonNull(args) && args.length > 0 ? args : DEFAULT_MSG_ARGS));
    }

    /**
     * @param message
     * @param cause
     * @param args
     * @return
     */
    default ApplicationException exceptionMsg(final String message, final Throwable cause,
                                              final Object... args) {

        return new ApplicationException(getCode(), MessageFormat.format(message,
                Objects.nonNull(args) && args.length > 0 ? args : DEFAULT_MSG_ARGS), cause);
    }
}
