package com.liquido.core.common.exception.resolver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import com.liquido.core.common.exception.ApplicationException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.exception.ExtendException;
import com.liquido.core.common.exception.NextAction;
import com.liquido.core.common.logger.LogOp;
import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.common.utils.WebUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.token.FormToken;
import com.liquido.core.mvc.token.TokenManager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * GlobalHandlerExceptionResolver
 */
@Slf4j
public class GlobalHandlerExceptionResolver extends AbstractGlobalHandlerExceptionResolver {

    /**
     * delimiter for error message content
     */
    public static final String ERROR_MSG_DELIMITER = "#";
    private static final String ERROR_CODE_ATTR_NAME = "code";
    private static final String ERROR_MSG_ATTR_NAME = "message";
    private static final String ARGUMENT_INVALID_ERROR_CODE = "501";
    private static final String DEFAULT_ERROR_MESSAGE = "System error";
    /**
     * Default error view name
     */
    @Value("${common.exception.error.view-name:error}")
    private String errorViewName;
    @Value("${common.exception.error.print-error:true}")
    private boolean printErrorLog;
    @Value("${common.exception.error.context-path:contextPath}")
    private String contextPath;
    @Value("${common.exception.error.error-code:500}")
    private String defaultErrorCode;

    @Value("${common.exception.security-enable:false}")
    private boolean securityExceptionEnable;

    private List<ExtendException> extendException;

    private MessageSource messageSource;

    private TokenManager tokenManager;

    /**
     * All exceptions are handled by default, if different behavior, can extend it in subclasses
     */
    @Override
    protected boolean support(final HttpServletRequest request,
                              final HttpServletResponse response,
                              final Object handler,
                              final Exception ex) {
        return true;
    }

    /**
     * record exception message
     */
    @Override
    protected void recordException(final Exception ex, final HttpServletRequest request) {
        if (printErrorLog) {
            log.error(LogWrapper.op(LogOp.EXP_RESOLVER_DEAL)
                    .msg("Api execute api fail")
                    .wrap("url", request.getRequestURI()).toString(), ex);
        } else {
            log.error(LogWrapper.op(LogOp.EXP_RESOLVER_DEAL)
                    .msg("Api execute api fail")
                    .wrap("url", request.getRequestURI())
                    .wrap("error:", ex.getMessage())
                    .toString());
        }
    }

    /**
     * Handle non-JSON requests, return the default ViewName
     */
    @Override
    protected ModelAndView resolveExceptionForNonJson(final HttpServletRequest request,
                                                      final HttpServletResponse response,
                                                      final Object handler,
                                                      final Exception ex) {
        final ModelAndView view = new ModelAndView();
        view.setViewName(errorViewName);

        try {
            final String errorCode = getErrorCode(request, ex);
            final String errorMessage = getErrorMessage(errorCode, ex);
            final Pair<String, String> pair = parseErrorMessage(errorMessage);

            view.addObject(ERROR_CODE_ATTR_NAME, errorCode);
            view.addObject(ERROR_MSG_ATTR_NAME, pair.getRight());
            view.addObject(contextPath, request.getContextPath());

            // Generate anti-duplicate commits token
            if (generateToken(handler)) {
                view.addObject(TokenManager.TOKEN_NAME, tokenManager.newToken());
            }

        } catch (Throwable ee) {
            log.error(LogWrapper.op(LogOp.EXP_RESOLVER_FAIL)
                    .msg("ResolveExceptionForNonJson Fail")
                    .toString(), ee);
        }
        return view;
    }

    /**
     * Handling Json requests
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     *
     * @return
     */
    @Override
    protected ModelAndView resolveExceptionForJson(final HttpServletRequest request,
                                                   final HttpServletResponse response,
                                                   final Object handler,
                                                   final Exception ex) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(WebUtil.JSON_CONTENT_TYPE);

            final ResponseDto jsonMessage = new ResponseDto();
            final String errorCode = getErrorCode(request, ex);
            final String errorMessage = getErrorMessage(errorCode, ex);
            final Pair<String, String> pair = parseErrorMessage(errorMessage);
            final Object errorContent = getErrorContent(ex);
            jsonMessage.setCode(Integer.parseInt(errorCode));
            //jsonMessage.setNext(pair.first());
            jsonMessage.setMsg(pair.getRight());
            if (errorContent != null) {
                jsonMessage.setData(errorContent);
            }

            // Generate anti-duplicate commits token
            if (generateToken(handler)) {
                jsonMessage.setToken(tokenManager.newToken());
            }

            final String responseText = WebUtil.toJsonpString(jsonMessage);
            log.info(LogWrapper.op("GlobalHandlerExceptionResolver.resolveExceptionForJson")
                    .msg("HTTP Error Response")
                    .wrap("result", responseText).toString());

            return writeOutput(response, responseText,
                    responseText.getBytes(StandardCharsets.UTF_8));
        } catch (Throwable e) {
            log.error(LogWrapper.op(LogOp.EXP_RESOLVER_FAIL)
                    .msg("resolveExceptionForJson Fail")
                    .toString(), e);
            return new ModelAndView();
        }
    }

    /**
     * get error content
     *
     * @param ex
     *
     * @return
     */
    protected Object getErrorContent(final Exception ex) {
        if (ex instanceof ApplicationException) {
            return ((ApplicationException) ex).getData();
        }
        return null;
    }

    /**
     * Determine whether it is necessary to generate an anti-duplicate submission token
     *
     * @param handler
     *
     * @return
     */
    private boolean generateToken(final Object handler) {
        if (tokenManager == null) {
            return false;
        }

        if (handler instanceof HandlerMethod) {
            final HandlerMethod hm = (HandlerMethod) handler;
            final Method method = hm.getMethod();
            final FormToken formToken = method.getAnnotation(FormToken.class);
            if (formToken == null) {
                return false;
            }

            if (formToken.generateToken()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Write the error message to the response body
     *
     * @param response
     * @param text
     * @param bytes
     *
     * @return
     */
    private ModelAndView writeOutput(final HttpServletResponse response,
                                     final String text,
                                     final byte[] bytes) throws IOException {
        try (final OutputStream out = response.getOutputStream()) {
            out.write(bytes);
            out.flush();
            return new ModelAndView();
        } catch (IllegalStateException e) {
            final String iseMsg = "getWriter() has already been called for this response";
            if (iseMsg.equals(e.getMessage())) {
                final Writer writer = response.getWriter();
                writer.write(text);
                writer.flush();
                return new ModelAndView();
            }
        }

        return new ModelAndView();
    }

    /**
     * parse error message
     *
     * @param errMsg
     *
     * @return Pair: The first parameter is used to assign to next step, and the second
     * parameter is used to assign to message
     */
    protected Pair<String, String> parseErrorMessage(final String errMsg) {
        if (StringUtils.isBlank(errMsg)) {
            return Pair.of(null, errMsg);
        }

        if (!errMsg.contains(ERROR_MSG_DELIMITER)) {
            return Pair.of(null, errMsg);
        }

        final int index = errMsg.indexOf(ERROR_MSG_DELIMITER);
        final String nextAction = errMsg.substring(0, index);
        final String msgRet = errMsg.substring(index + 1);

        if (NextAction.isExists(nextAction)) {
            return Pair.of(nextAction, msgRet);
        } else {
            log.warn(LogWrapper.op(LogOp.EXP_NEXT_ACTION_INVALID)
                    .msg("nextAction invalid")
                    .wrap("errMsg", errMsg)
                    .wrap("nextAction", nextAction).toString());
            return Pair.of(null, msgRet);
        }
    }

    /**
     * get error code
     *
     * @param request
     * @param ex
     *
     * @return
     */
    protected String getErrorCode(final HttpServletRequest request, final Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            return ARGUMENT_INVALID_ERROR_CODE;
        }

        if (securityExceptionEnable) {
            if (ex instanceof AccessDeniedException) {
                return CommonExceptionCode.PERMISSION_DENIED.getCode() + "";
            }

            if (ex instanceof InternalAuthenticationServiceException) {
                return CommonExceptionCode.INCORRECT_ACCOUNT.getCode() + "";
            }

            if (ex instanceof BadCredentialsException) {
                return CommonExceptionCode.INCORRECT_ACCOUNT.getCode() + "";
            }
        }

        if (ex instanceof ApplicationException) {
            final Integer code = ((ApplicationException) ex).getCode();
            if (Objects.isNull(code)) {
                return defaultErrorCode;
            } else {
                return String.valueOf(code);
            }
        }
        if (ex instanceof ApplicationException) {
            final Integer code = ((ApplicationException) ex).getCode();
            if (Objects.isNull(code)) {
                return defaultErrorCode;
            } else {
                return String.valueOf(code);
            }
        }

        // TODO Extend exceptions not defined within the framework
        return defaultErrorCode;
    }

    /**
     * get error message
     *
     * @param errorCode
     * @param ex
     *
     * @return
     */
    protected String getErrorMessage(final String errorCode, final Exception ex) {
        String retMessage = null;
        if (securityExceptionEnable) {
            if (ex instanceof AccessDeniedException) {
                return CommonExceptionCode.PERMISSION_DENIED.getMessage();
            }
            if (ex instanceof InternalAuthenticationServiceException) {
                return CommonExceptionCode.INCORRECT_ACCOUNT.getMessage();
            }
            if (ex instanceof BadCredentialsException) {
                return CommonExceptionCode.INCORRECT_ACCOUNT.getMessage();
            }
        }

        if (ex instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) ex)
                    .getBindingResult().getFieldErrors().stream()
                    .filter(x -> StringUtils.isNotBlank(x.getDefaultMessage()))
                    .map(x -> String.format("'%s' %s", x.getField(), x.getDefaultMessage()))
                    .findFirst().orElseThrow(CommonExceptionCode.SYSTEM_ERROR::exception);
        }

        if (ex instanceof ConstraintViolationException) {
            return ((ConstraintViolationException) ex)
                    .getConstraintViolations().stream()
                    .filter(x -> StringUtils.isNotBlank(x.getMessage()))
                    .map(x -> String.format("'%s' %s", x.getPropertyPath(), x.getMessage()))
                    .findFirst().orElseThrow(CommonExceptionCode.SYSTEM_ERROR::exception);
        }

        if (messageSource != null) {
            retMessage = getErrMsgFromSource(errorCode, ex);
        }

        if (StringUtils.isBlank(retMessage)) {
            if (ex instanceof ApplicationException) {
                retMessage = getErrMsgFromSelf(errorCode, ex);
            }
            // TODO Extend exceptions not defined within the framework
        }
        return StringUtils.defaultIfBlank(retMessage, ex.getMessage());
    }

    /**
     * Get error information from exception instance
     *
     * @param errorCode
     * @param ex
     *
     * @return
     */
    private String getErrMsgFromSelf(final String errorCode, final Exception ex) {
        String retMessage;
        if (StringUtils.isNotBlank(ex.getMessage())) {
            retMessage = ex.getMessage();
        } else {
            if (ex.getCause() != null) {
                retMessage = ex.getCause().getMessage();
            } else {
                retMessage = ARGUMENT_INVALID_ERROR_CODE;
                log.error(LogWrapper.op(LogOp.EXP_RESOLVER_FAIL)
                        .msg(ex.getClass().getSimpleName() + " no message")
                        .wrap("code", errorCode)
                        .toString(), ex);
            }
        }
        return retMessage;
    }

    /**
     * Get error messages from messageSource in many different ways
     *
     * @param errorCode
     * @param ex
     *
     * @return
     */
    private String getErrMsgFromSource(final String errorCode, final Exception ex) {
        String retMessage = null;
        final Class<?> clz = ex.getClass();
        if (ex instanceof ApplicationException) {
            retMessage = getErrMsgOfAppException(errorCode, ex, clz);
        }

        if (retMessage == null) {
            retMessage = getErrMsgOfNonAppException(ex, clz);
        }

        return retMessage;
    }

    /**
     * Get the error message of ApplicationException from messageSource
     *
     * @param errorCode
     * @param ex
     * @param clz
     *
     * @return
     */
    private String getErrMsgOfAppException(final String errorCode,
                                           final Exception ex,
                                           final Class<?> clz) {
        final ApplicationException ae = (ApplicationException) ex;
        String msgKey = errorCode;
        String retMessage = getMessageFromSource(msgKey, ae.getArgs());
        if (StringUtils.isBlank(retMessage)) {
            msgKey = clz.getName() + "." + errorCode;
            retMessage = getMessageFromSource(msgKey, ae.getArgs());
        }

        if (StringUtils.isBlank(retMessage)) {
            msgKey = clz.getSimpleName() + "." + errorCode;
            retMessage = getMessageFromSource(msgKey, ae.getArgs());
        }

        return retMessage;
    }

    /**
     * Get non-ApplicationException error information from messageSource
     *
     * @param ex
     * @param clz
     *
     * @return
     */
    private String getErrMsgOfNonAppException(final Exception ex, final Class<?> clz) {

        String msgKey = clz.getName();
        String message = getMessageFromSource(msgKey, null);
        if (StringUtils.isBlank(message)) {
            msgKey = clz.getSimpleName();
            message = getMessageFromSource(msgKey, null);
        }

        return message;
    }

    /**
     * Get a single error message from messageSource
     *
     * @param errorCode
     * @param args
     *
     * @return
     */
    private String getMessageFromSource(final String errorCode, final Object[] args) {
        String retMessage = null;
        try {
            retMessage = messageSource.getMessage(errorCode, args, Locale.ENGLISH);
        } catch (NoSuchMessageException nmex) {
            log.warn("error code not in message source, code=" + errorCode);
        }
        return retMessage;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public void setTokenManager(final TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public List<ExtendException> getExtendException() {
        return extendException;
    }

    public void setExtendException(final List<ExtendException> extendException) {
        this.extendException = extendException;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
