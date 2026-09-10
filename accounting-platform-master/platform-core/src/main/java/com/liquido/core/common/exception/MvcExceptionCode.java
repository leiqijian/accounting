package com.liquido.core.common.exception;

import java.util.Objects;

import com.liquido.core.mvc.dto.ResponseDto;

/**
 * MVC exception error code enumeration
 * <p>
 * <ul>
 * <li>Error code range value: 2000~2999</li>
 * </ul>
 * </p >
 */
public enum MvcExceptionCode implements ErrorWrapper {

    ACCESS_DENY(2000, "access is denied"),

    TOKEN_CHECK_ERROR(2001, "access token invalid"),

    SIGNATURE_ERROR(2002, "parameter signature failed"),

    REPEAT_SUBMIT_ERROR(2010, "repeated submit error"),

    DUPLICATE_NOTIFY_ERROR(2011, "repeat notifications error"),

    FREQUENT_REQUESTS_ERROR(2020, "too frequent requests, please wait"),

    ;

    private Integer code;

    private String message;

    MvcExceptionCode(final Integer code, final String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static MvcException reException(final Integer code, final String message) {
        return new MvcException(code, message);
    }

    public static MvcException reException(final ResponseDto<?> vo) {
        if (Objects.isNull(vo)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        return reException(vo.getCode(), vo.getMsg());
    }
}
