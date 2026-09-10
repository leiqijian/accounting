package com.liquido.core.common.exception;

import java.util.Objects;

import com.liquido.core.mvc.dto.ResponseDto;

/**
 * framework exception error code enumeration
 * <ul>
 * <li>Error code range value: 1000~1999 </li>
 * </ul>
 * </p >
 */
public enum FrameworkExceptionCode implements ErrorWrapper {

    SNOWFLAKE_SWITCH_OFF(1000, "snowflake algorithm is not configured or not enabled"),

    SNOWFLAKE_CONFIG_ERROR(1001,
            "snowflake algorithm configuration exception, please check the configuration"),

    SNOWFLAKE_CENTER_ID_ERROR(1002, "the maximum value of snowflake algorithm center-id is 8"),

    SNOWFLAKE_WORKER_ID_ERROR(1003, "the maximum value of snowflake algorithm worker-id is 1024"),

    SNOWFLAKE_NET_ADDRESS_ERROR(1004,
            "cannot get localhost net-address, please check your network!"),

    BEAN_PROPERTIES_COPY_ERROR(1100, "bean properties copy fail."),

    ;

    private final Integer code;

    private String message;

    FrameworkExceptionCode(final Integer code, final String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static FrameworkException reException(final Integer code, final String message) {
        return new FrameworkException(code, message);
    }

    public static FrameworkException reException(final ResponseDto<?> vo) {
        if (Objects.isNull(vo)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        return reException(vo.getCode(), vo.getMsg());
    }
}
