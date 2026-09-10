package com.liquido.core.common.exception.resolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liquido.core.common.utils.WebUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * Global exception handling
 */
public abstract class AbstractGlobalHandlerExceptionResolver
        extends AbstractHandlerExceptionResolver {

    /**
     * Whether RESTFul service, if yes, all requests are JSON requests by default
     */
    protected Boolean restService;

    @Resource
    private ApplicationContext context;

    /**
     * check whether the exception handler supports the current exception
     *
     * @param request
     * @param response
     * @param handler
     * @param exception
     * @return
     */
    protected abstract boolean support(final HttpServletRequest request,
                                       final HttpServletResponse response,
                                       final Object handler,
                                       final Exception exception);

    @Override
    protected ModelAndView doResolveException(final HttpServletRequest request,
                                              final HttpServletResponse response,
                                              final Object handler,
                                              final Exception exception) {

        if (!support(request, response, handler, exception)) {
            return null;
        }

        // Record exception information
        recordException(exception, request);

        final boolean isJsonRequest = restService ? true : WebUtil.isJsonRequest(request, handler);
        if (isJsonRequest) {
            return resolveExceptionForJson(request, response, handler, exception);
        }

        return resolveExceptionForNonJson(request, response, handler, exception);
    }

    /**
     * Record exception information
     *
     * @param ex
     * @param request
     */
    protected abstract void recordException(final Exception ex, final HttpServletRequest request);

    /**
     * Exception parsing Json request
     *
     * @param request
     * @param response
     * @param handler
     * @param exception
     * @return
     */
    protected abstract ModelAndView resolveExceptionForJson(final HttpServletRequest request,
                                                            final HttpServletResponse response,
                                                            final Object handler,
                                                            final Exception exception);

    /**
     * Handling non-Json request exceptions
     *
     * @param request
     * @param response
     * @param handler
     * @param exception
     * @return
     */
    protected abstract ModelAndView resolveExceptionForNonJson(final HttpServletRequest request,
                                                               final HttpServletResponse response,
                                                               final Object handler,
                                                               final Exception exception);

    /**
     * @param restService the restService to set
     */
    public void setRestService(final Boolean restService) {
        this.restService = restService == null ? false : restService;
    }
}
