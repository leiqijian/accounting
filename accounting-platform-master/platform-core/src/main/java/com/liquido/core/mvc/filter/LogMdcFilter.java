package com.liquido.core.mvc.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liquido.core.common.logger.LogConstant;
import com.liquido.core.common.utils.DataUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

/**
 * MDC filter
 */
@Slf4j
public class LogMdcFilter extends CommonFilter {

    @Value("${spring.application.name:}")
    private String appName;

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String traceId = request.getHeader(LogConstant.TRACE_ID);
            if (StringUtils.isBlank(traceId)) {
                traceId = DataUtil.getUuid();
            }

            String headerAppName = request.getHeader(LogConstant.APP_NAME);
            if (StringUtils.isBlank(headerAppName)) {
                headerAppName = this.appName;
            }

            MDC.put(LogConstant.URL, request.getServletPath());
            MDC.put(LogConstant.TRACE_ID, traceId);

            response.addHeader(LogConstant.TRACE_ID, traceId);
            response.addHeader(LogConstant.APP_NAME, headerAppName);

            log.info("LogMdcFilter Request Uri={}", request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
