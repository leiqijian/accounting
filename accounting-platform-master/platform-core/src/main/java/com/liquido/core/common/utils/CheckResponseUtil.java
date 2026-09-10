package com.liquido.core.common.utils;

import java.util.Objects;

import com.liquido.core.common.exception.BusinessException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.mvc.dto.ResponseDto;

import org.apache.commons.lang3.ObjectUtils;

public class CheckResponseUtil {

    public static void checkResponse(final ResponseDto<?> responseDto) {
        if (Objects.isNull(responseDto)) {
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        } else if (responseDto.isFail()) {
            throw new BusinessException(responseDto.getCode(), responseDto.getMsg());
        }
    }

    public static void checkResponseData(final ResponseDto<?> responseDto) {
        checkResponse(responseDto);
        if (ObjectUtils.isEmpty(responseDto.getData())) {
            throw CommonExceptionCode.DATA_NOT_FOUND.exception();
        }
    }

    public static <T> T checkAndReturnResponseData(final ResponseDto<T> responseDto) {
        checkResponse(responseDto);
        if (ObjectUtils.isEmpty(responseDto.getData())) {
            throw CommonExceptionCode.DATA_NOT_FOUND.exception();
        }
        return responseDto.getData();
    }

}
