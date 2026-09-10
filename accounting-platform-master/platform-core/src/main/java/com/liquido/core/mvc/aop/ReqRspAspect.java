package com.liquido.core.mvc.aop;

import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.common.logger.LoggerSwitch;
import com.liquido.core.configuration.CommonProperties;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * http request response Aspect
 */
@Slf4j
@Aspect
public class ReqRspAspect {

    private static final List<String> SKIP_URL_LIST =
            Lists.newArrayList("/checkhealth", "/check/health", "/check-health", "/error");
    @Resource
    private CommonProperties commonProperties;

    private static boolean skip(final String uri) {
        return SKIP_URL_LIST.contains(uri);
    }

    /**
     * Exclude Argument
     */
    private static boolean isExcludeArgument(final Object arg,
                                             final List<CommonProperties.LogSens> logSens) {
        if (HttpServletRequest.class.isAssignableFrom(arg.getClass())) {
            return true;
        }

        if (HttpServletResponse.class.isAssignableFrom(arg.getClass())) {
            return true;
        }

        if (BindingResult.class.isAssignableFrom(arg.getClass())) {
            return true;
        }

        if (MultipartFile.class.isAssignableFrom(arg.getClass())) {
            return true;
        }

        if (CollectionUtils.isNotEmpty(logSens)) {
            for (final CommonProperties.LogSens exclude : logSens) {
                if (exclude.getExcludeClass().isAssignableFrom(arg.getClass())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Pointcut("execution(public * com..*.controller..*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object aroundAdvice(final ProceedingJoinPoint pjp) throws Throwable {
        final long startTimestamp = System.currentTimeMillis();

        final MethodSignature ms = (MethodSignature) pjp.getSignature();
        final Method method = ms.getMethod();
        if (AnnotationUtils.findAnnotation(method, RequestMapping.class) == null) {
            return pjp.proceed();
        }

        final RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        final HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
        final Object[] arguments = pjp.getArgs();

        if (skip(request.getServletPath())) {
            return pjp.proceed();
        }

        final LoggerSwitch logSwitch = AnnotationUtils.findAnnotation(method, LoggerSwitch.class);
        if (null == logSwitch || logSwitch.request()) {
            log.info(LogWrapper.op("ReqRspAspect.aroundAdvice")
                    .msg("[HTTP {} Request]")
                    .wrap("requestData",
                            ArrayUtils.isNotEmpty(arguments) ? getArguments(arguments) :
                                    Lists.newArrayList()).toString(), request.getMethod());
        }

        // The return value of the intercepted method
        Object result = null;
        try {
            result = pjp.proceed();
        } finally {
            if (null == logSwitch || logSwitch.response()) {
                log.info(LogWrapper.op("ReqRspAspect.aroundAdvice")
                                .msg("[HTTP {} Response], consume: {} ms ")
                                .wrap("responseData", result).toString(), request.getMethod(),
                        (System.currentTimeMillis() - startTimestamp));
            }
        }

        return result;
    }

    private List<Object> getArguments(final Object[] arguments) {
        final List<Object> args = Lists.newArrayList();
        final List<CommonProperties.LogSens> logSens = commonProperties.getLogSens();
        for (final Object arg : arguments) {
            if (arg == null || isExcludeArgument(arg, logSens)) {
                continue;
            }
            args.add(arg);
        }

        return args;
    }
}
