package com.liquido.core.common.utils;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import com.liquido.core.common.constant.Constant;

public class RequestUtil {

    public static boolean isInternalRequest(final HttpServletRequest request) {
        return Objects.nonNull(request)
                && Objects.nonNull(request.getHeader(Constant.Header.SERVICE_FROM));
    }
}
