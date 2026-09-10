package com.liquido.core.common.utils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(
            final ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(final Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }

    public static Object getBean(final String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public static  <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
        return applicationContext.getBeansOfType(type);
    }
}
