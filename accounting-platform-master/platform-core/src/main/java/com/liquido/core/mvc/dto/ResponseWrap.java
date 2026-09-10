package com.liquido.core.mvc.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.liquido.core.common.exception.ErrorWrapper;
import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.mvc.vo.DataList;

import lombok.extern.slf4j.Slf4j;

/**
 * response body wrapper
 */
@Slf4j
public class ResponseWrap implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * response body wrapper
     *
     * @param error error
     */
    public static <T> ResponseDto<T> failure(final ErrorWrapper error) {
        return error.parse();
    }

    /**
     * response body wrapper
     *
     * @param content content
     */
    public static <T> ResponseDto<T> response(final T content) {
        return ResponseDto.success(content);
    }

    public static <T> ResponseDto<DataList<T>> response(final Collection<T> dataList) {
        return ResponseDto.success(new DataList<>(dataList));
    }

    /**
     * check response
     *
     * @param response response
     */
    public static <T> void checkResponse(final ResponseDto<T> response,
                                         final ErrorWrapper error) {
        log.info(LogWrapper.op("ResponseWrap.checkResponse").msg("check response")
                .wrap("response", response).toString());

        Optional.ofNullable(response).filter(value -> value.getCode() == ResponseDto.SUCCESS_CODE)
                .orElseThrow(error::exception);
    }

    /**
     * get response content(throw exception)
     *
     * @param response response
     */
    public static <T> T getResponse(final ResponseDto<T> response, final ErrorWrapper error) {
        log.info(LogWrapper.op("ResponseWrap.checkResponse").msg("check response")
                .wrap("response", response).toString());

        return Optional.ofNullable(response)
                .filter(value -> value.getCode() == ResponseDto.SUCCESS_CODE)
                .map(ResponseDto::getData).orElseThrow(error::exception);
    }

    /**
     * check response(unchecked response content, not throw exception)
     *
     * @param response response
     * @return boolean
     */
    public static <T> boolean checkResponse(final ResponseDto<T> response) {
        final String op = "ResponseWrap.checkResponse";
        log.info(LogWrapper.op(op).msg("check response").wrap("response", response).toString());

        final boolean flag = Optional.ofNullable(response)
                .filter(value -> value.getCode() == ResponseDto.SUCCESS_CODE).isPresent();
        if (!flag) {
            log.error(
                    LogWrapper.op(op).msg("response error").wrap("response", response).toString());
        }

        return flag;
    }

    /**
     * check response(unchecked response content, not throw exception)
     *
     * @param response response
     */
    public static <T> Optional<T> getResponse(final ResponseDto<T> response) {
        log.info(LogWrapper.op("ResponseWrap.getResponse").msg("check response")
                .wrap("response", response).toString());
        final Optional<ResponseDto<T>> optional = Optional.ofNullable(response)
                .filter(value -> value.getCode() == ResponseDto.SUCCESS_CODE)
                .filter(value -> Objects.nonNull(value.getData()));

        if (optional.isEmpty()) {
            log.error(
                    LogWrapper.op("ResponseWrap.getResponse").msg("call service fail").toString());
            return Optional.empty();
        }

        return optional.map(ResponseDto::getData);
    }
}
