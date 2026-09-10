package com.liquido.aqueducts.interceptor;

import com.liquido.aqueducts.commons.ResponseData;
import com.liquido.aqueducts.commons.enums.ResultCode;
import com.liquido.aqueducts.commons.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseData<Object>> businessExceptionHandler(final BusinessException e) {
        final ResponseData<Object> responseData = new ResponseData();
        responseData.setCode(e.getCode());
        if (ResultCode.fromCode(e.getCode()) != null) {
            responseData.setMessage(ResultCode.fromCode(e.getCode()).getMessage());
        } else {
            responseData.setMessage(e.getMessage());
        }

        Map<String, String> data = new LinkedHashMap<>();
        data.put("errorMessage", e.getMessage());
        if (e.getCause() != null) {
            data.put("cause", e.getCause().getMessage());
        }
        responseData.setData(data);
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseData<Object>> parameterException(final MissingServletRequestParameterException e) {
        final ResponseData<Object> responseData = new ResponseData();
        responseData.setCode(ResultCode.INVALID_PARAMETERS.getCode());
        responseData.setMessage(ResultCode.INVALID_PARAMETERS.getMessage());
        Map<String, String> data = new LinkedHashMap<>();
        data.put("errorMessage", e.getMessage());
        data.put("parameterName", e.getParameterName());
        data.put("parameterType", e.getParameterType());
        responseData.setData(data);
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> unknownExceptionHandler(final Exception e) {
        e.printStackTrace();
        final ResponseData<Object> responseData = new ResponseData();
        responseData.setCode(ResultCode.UNKNOWN_ERROR.getCode());
        responseData.setMessage(ResultCode.UNKNOWN_ERROR.getMessage());
        Map<String, String> data = new LinkedHashMap<>();
        data.put("errorMessage", e.getMessage());
        if (e.getCause() != null) {
            data.put("cause", e.getCause().getMessage());
        }
        responseData.setData(data);
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

}
