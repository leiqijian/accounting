package com.liquido.aqueducts.commons.aop;

import com.liquido.aqueducts.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class InterfaceMonitor {

    @SneakyThrows
    @Around("execution( * com.liquido.aqueducts.controller.*.*(..)) && " +
            "!execution( * com.liquido.aqueducts.controller.HealthCheckController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        StopWatch watch = new StopWatch();
        String methodName = joinPoint.getSignature().getName();
        watch.start(methodName);
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final HttpServletRequest request = attributes.getRequest();
        final String requestURI = request.getRequestURI();
        String requestBody;
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            requestBody = Arrays.toString(joinPoint.getArgs());
        } else {
            requestBody = JsonUtil.mapToJsonString(request.getParameterMap());
        }
        log.info("interface {} start", requestURI);
        Object obj = joinPoint.proceed();
        watch.stop();
        log.info("Interface: {}, Param: {} Spend time: {} seconds", requestURI, requestBody,
                watch.getTotalTimeSeconds());
        return obj;
    }
}
