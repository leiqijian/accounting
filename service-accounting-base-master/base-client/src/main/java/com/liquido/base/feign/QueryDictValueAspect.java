package com.liquido.base.feign;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class QueryDictValueAspect {

    private static final ThreadLocal<Class<?>> CLASS_TYPE = new ThreadLocal<>();

    public static Class<?> getClassType() {
        return CLASS_TYPE.get();
    }

    @Around("execution(* com.liquido.base.api.DictionaryApi.queryDictValue("
            + "com.liquido.base.enums.DictionaryTypeEnum, Class, String, String...))")
    public Object process(final ProceedingJoinPoint pjp) throws Throwable {
        try {
            CLASS_TYPE.set((Class<?>) pjp.getArgs()[1]);
            return pjp.proceed();
        } finally {
            CLASS_TYPE.remove();
        }
    }
}
