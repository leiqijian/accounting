package com.liquido.core.mvc.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * response body wrapper
 */
@Data
public class ResponseDto<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * default response success code, default:200
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * default response success msg, default:success
     */
    public static final String SUCCESS_MSG = "success";

    /**
     * response code
     */
    private Integer code;

    /**
     * error message
     */
    private String msg;

    /**
     * response result data
     */
    private T data;

    /**
     * token
     */
    private String token;

    public ResponseDto() {

    }

    /**
     * response success
     *
     * @return response
     */
    public static <T> ResponseDto<T> success() {
        final ResponseDto<T> resp = new ResponseDto<>();
        resp.code = SUCCESS_CODE;
        resp.msg = SUCCESS_MSG;
        return resp;
    }

    /**
     * response success with data
     *
     * @param content data
     *
     * @return response
     */
    public static <T> ResponseDto<T> success(final T content) {
        final ResponseDto<T> resp = ResponseDto.success();
        resp.code = SUCCESS_CODE;
        resp.msg = SUCCESS_MSG;
        resp.data = content;
        return resp;
    }

    /**
     * response success with content and message
     *
     * @param content data
     * @param message msg
     *
     * @return response
     */
    public static <T> ResponseDto<T> success(final T content, final String message) {
        final ResponseDto<T> resp = ResponseDto.success();
        resp.code = SUCCESS_CODE;
        resp.msg = Optional.ofNullable(message).map(String::trim).orElse(SUCCESS_MSG);
        resp.data = content;
        return resp;
    }

    /**
     * response fail with code and message
     *
     * @param code    error code
     * @param message msg
     *
     * @return response
     */
    @SuppressWarnings("rawtypes")
    public static ResponseDto fail(final Integer code, final String message) {
        final ResponseDto rsp = new ResponseDto<>();
        rsp.code = code;
        rsp.msg = Optional.ofNullable(message).map(String::trim).orElse("fail");
        return rsp;
    }

    /**
     * new instance with code, message, content
     *
     * @param code    error code
     * @param message msg
     * @param content data
     *
     * @return response
     */
    public static <T> ResponseDto<T> newInstance(final Integer code,
                                                 final String message,
                                                 final T content) {
        final ResponseDto<T> rsp = new ResponseDto<>();
        rsp.code = code;
        rsp.data = content;
        rsp.msg = Optional.ofNullable(message).map(String::trim).orElse("");
        return rsp;
    }

    /**
     * check result is success
     *
     * @return success status
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.getCode() == SUCCESS_CODE;
    }

    /**
     * check result is fail
     *
     * @return fail status
     */
    @JsonIgnore
    public boolean isFail() {
        return this.getCode() != SUCCESS_CODE;
    }

    public static boolean isFail(final ResponseDto responseDto) {
        return (Objects.isNull(responseDto) || responseDto.isFail());
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

}
