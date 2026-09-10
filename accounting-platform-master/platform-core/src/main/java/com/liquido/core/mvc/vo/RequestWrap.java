package com.liquido.core.mvc.vo;

import java.io.Serializable;

import lombok.extern.slf4j.Slf4j;

/**
 * request body wrapper
 */
@Slf4j
public class RequestWrap implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * request body wrapper
     *
     * @param params
     * @param ext
     * @return
     */
    public static <T> RequestVo<T> request(final T params, final String ext) {
        final RequestVo<T> req = new RequestVo<>();
        req.setParams(params);
        req.setExtend(ext);
        return req;
    }

    /**
     * request body wrapper
     *
     * @param params
     * @return
     */
    public static <T> RequestVo<T> request(final T params) {
        return request(params, "");
    }
}
