package com.liquido.core.common.exception;

import java.util.Objects;

import com.liquido.core.mvc.dto.ResponseDto;

/**
 * common exception error code enumeration
 * <p>
 * <ul>
 * <li>Error code range value: 1~999</li>
 * </ul>
 * </p >
 */
public enum CommonExceptionCode implements ErrorWrapper {

    FAILURE(1, "Fail"),

    PARAMETER_ILLEGAL(2, "Parameter invalid: {0}"),

    PARAMETER_MISSING(3, "Parameter missing: {0}"),

    PARAMETER_ILLEGAL_BLANK(4, "Parameter invalid, the argument cannot be null or empty string"),

    PARAMETER_ILLEGAL_NULL(5, "Parameter invalid, the argument cannot be null or empty"),

    PARAMETER_SIGNATURE_INCORRECT(6, "Parameter signature incorrect"),

    DATA_NOT_FOUND(7, "Data not found"),

    ENUM_PARSE_ERROR(8, "Enum parse error, to parse data: {0}"),

    FILE_NOT_FOUND(9, "File not fund"),

    FILE_UPLOAD_FAIL(10, "File upload fail"),

    FILE_DOWNLOAD_FAIL(11, "File download fail"),

    FILE_EXPORT_FAIL(20, "File export fail"),

    FILE_IMPORT_FAIL(21, "File import fail"),

    TYPE_CONVERSION_FAIL(22, "type conversion fail: {0}"),

    DECRYPTION_FAIL(23, "{0} decryption failed"),

    INVALID_FILENAME(24, "File upload fail, invalid fileName:{0}"),

    INVALID_FILETYPE(25, "File upload fail, fileType not support:{0}"),

    FILE_DELETE_FAIL(26, "File delete fail"),

    MAILBOX_INVALID(30, "The opposite mailbox is not set or the format is invalid"),

    UNAUTHENTICATED(401, "Unauthenticated"),

    PERMISSION_DENIED(403, "Access denied"),

    REQUEST_TIMEOUT(408, "Request timeout"),

    SYSTEM_ERROR(500, "The system error"),

    ENV_CONFIG_MISSING(599, "Environment config missing:{0}"),

    UNSUPPORTED_FEATURES(600, "Unsupported features"),

    INVALID_CARD_NUMBER(700, "Invalid card number"),

    SERVICE_IS_BUSY_ERROR(999, "Service is busy, please try again later"),

    INCORRECT_ACCOUNT(1000, "Login fail"),

    SYSTEM_UPGRADING(9999,
            "System or service upgrade, please contact customer service for details"),

    SYSTEM_SERVICE_ERROR(99999, "The system service error"),

    ;

    private Integer code;

    private String message;

    CommonExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static CommonException reException(final Integer code, final String message) {
        return new CommonException(code, message);
    }

    public static CommonException reException(final ResponseDto<?> vo) {
        if (Objects.isNull(vo)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        return reException(vo.getCode(), vo.getMsg());
    }
}
