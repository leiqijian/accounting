package com.liquido.core.mvc.token;

import javax.servlet.http.HttpServletRequest;

import com.liquido.core.common.exception.MvcExceptionCode;
import com.liquido.core.common.logger.LogOp;
import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

/**
 * Form Token Aspect
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FormTokenAspect {

    private final TokenManager tokenManager;
    private final HttpServletRequest request;

    @Around("execution(public * com..*.controller..*.*(..)) && @annotation(formToken)")
    public Object aroundAdvice(final ProceedingJoinPoint pjp, final FormToken formToken) throws Throwable {

        if (formToken.checkToken()) {
            final String token = request.getParameter(TokenManager.TOKEN_NAME);
            if (StringUtils.isBlank(token)) {
                log.error(
                        LogWrapper.op(LogOp.TOKEN_CHECK).msg("request token is empty").toString());
                throw MvcExceptionCode.TOKEN_CHECK_ERROR.exception();
            }

            final boolean checked = tokenManager.checkAndDelToken(token);
            if (!checked) {
                log.error(LogWrapper.op(LogOp.TOKEN_CHECK).msg("check token incorrect!")
                        .wrap("request token", token)
                        .wrap("real token", tokenManager.getToken(token)).toString());
                throw MvcExceptionCode.TOKEN_CHECK_ERROR.exception();
            }

            if (!formToken.generateToken()) {
                return processNewToken(pjp, true);
            }
        }

        /** set Token */
        if (formToken.generateToken()) {
            return processNewToken(pjp, false);
        }

        return pjp.proceed();
    }

    /**
     * generic new token
     *
     * @param pjp
     * @param checkCode
     * @return
     */
    private Object processNewToken(final ProceedingJoinPoint pjp, final boolean checkCode)
            throws Throwable {

        final String token = tokenManager.newToken();
        final Object retValue = pjp.proceed();
        if (retValue instanceof ResponseDto) {
            final ResponseDto op = (ResponseDto) retValue;
            if (!checkCode) {
                op.setToken(token);
            } else if (isFailureCode(String.valueOf(op.getCode()))) {
                op.setToken(token);
            }
        } else if (retValue instanceof ModelAndView) {
            final ModelAndView op = (ModelAndView) retValue;
            if (!checkCode) {
                op.addObject(TokenManager.TOKEN_NAME, token);
            } else if (isFailureCode(String.valueOf(op.getModel().get("code")))) {
                op.addObject(TokenManager.TOKEN_NAME, token);
            }
        } else {
            request.setAttribute(TokenManager.TOKEN_NAME, token);
        }
        return retValue;
    }

    private boolean isFailureCode(final String code) {
        if (tokenManager.getSucCodeList() == null) {
            return true;
        }
        return !tokenManager.getSucCodeList().contains(code);
    }
}
