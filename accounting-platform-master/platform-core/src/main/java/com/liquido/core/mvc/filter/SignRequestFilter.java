package com.liquido.core.mvc.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liquido.core.common.exception.ApplicationException;
import com.liquido.core.common.exception.BusinessException;
import com.liquido.core.common.exception.CommonException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.exception.FrameworkException;
import com.liquido.core.common.exception.MvcException;
import com.liquido.core.common.exception.resolver.GlobalHandlerExceptionResolver;
import com.liquido.core.mvc.crypto.SignatureScanner;
import com.liquido.core.mvc.crypto.SignatureStrategy;
import com.liquido.core.mvc.vo.AccessPartnerVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sign Request Filter
 */
@Slf4j
@RequiredArgsConstructor
public class SignRequestFilter extends CommonFilter {
    private final SignatureScanner signatureScanner;
    private final GlobalHandlerExceptionResolver resolver;

    /**
     * exception conversion
     */
    private static ApplicationException transferException(Exception e) {
        if (e instanceof MvcException) {
            return (MvcException) e;
        }

        if (e instanceof BusinessException) {
            return (BusinessException) e;
        }

        if (e instanceof CommonException) {
            return (CommonException) e;
        }

        if (e instanceof FrameworkException) {
            return (FrameworkException) e;
        }

        if (e instanceof ApplicationException) {
            return (ApplicationException) e;
        }

        return CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) {
        try {
            if (signatureScanner.needSignRequest(request.getRequestURI())) {

                final String uri = request.getRequestURI().trim();

                final SignatureStrategy signatureStrategy = signatureScanner.getSignStrategy(uri);

                final AccessPartnerVo assessPartner = signatureScanner.getAssessPartner(uri);

                final SignRequestWrapper requestWrapper =
                        new SignRequestWrapper(request, signatureStrategy, assessPartner);

                filterChain.doFilter(requestWrapper, response);
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            resolver.resolveException(request, response, null, transferException(e));
        }
    }
}
