package com.liquido.base.exception;

import java.util.Objects;

import com.liquido.core.common.exception.BusinessException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.exception.ErrorWrapper;
import com.liquido.core.mvc.dto.ResponseDto;

/**
 * Worker service error code
 * code range: [100000~149999]
 */
public enum BaseExceptionCode implements ErrorWrapper {

    SOME_ERROR(100000, "Some error"),
    PAYMENT_CHANNEL_UNDEFINED(100001, "Payment channel undefined"),

    INSTALLMENT_PARAMETER_ILLEGAL(100002, "Installment parameter illegal: {0}"),
    DYNAMIC_CONSTANT_PARSE_ERROR(100003, "Dynamic constant parse error class: {0}, code: {1}"),
    DICTIONARY_DUPLICATE_KEY_ERROR(100004, "Dictionary duplicate key error"),
    COST_CONFIG_NOT_FOUND(100005, "Cost config not exist"),
    COST_CONFIG_OCCUPY(100006, "Cost config have been occupied"),
    FIELD_REQUIRED(1000067, "Field {0} is required"),
    FIELD_DUPLICATED(100008, "Field {0} is duplicated"),
    PAYMENT_CONFIG_ALREADY_EXISTS(100009, "Account payment config already exists"),

    SEND_MESSAGE_TO_LARK_USER_FAIL(1000100, "send message to lark user fail:{0}"),
    BATH_SEND_MESSAGE_TO_LARK_USER_FAIL(1000101, "bath send message to lark user fail:{0}"),
    BATH_SEND_MESSAGE_FAIL_RECEIVER_OR_DEPARTMENT_NOT_NULL(1000103,
            "bath send message fail,receiver or department not null"),

    ;

    /* error code */
    private final Integer code;

    /* error description */
    private final String message;

    BaseExceptionCode(final Integer code, final String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static BusinessException reException(final Integer code, final String message) {
        return new BusinessException(code, message);
    }

    public static BusinessException reException(final ResponseDto<?> vo) {
        if (Objects.isNull(vo)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        return reException(vo.getCode(), vo.getMsg());
    }
}
