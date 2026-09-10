package com.liquido.base.constant.dynamic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.core.common.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DynamicConstantSupport {

    private static final Map<Class<? extends DynamicConstant<?>>,
            DynamicConstantFactory> FACTORIES = new ConcurrentHashMap<>();

    private static final String CODE_FIELD_NAME = "code";

    private DynamicConstantSupport() {
    }

    private static <C extends DynamicConstant<T>, T> DynamicConstantFactory getFactory(
            final Class<C> constantClass) {
        DynamicConstantFactory factory = FACTORIES.get(constantClass);
        if (Objects.nonNull(factory)) {
            return factory;
        }
        synchronized (FACTORIES) {
            factory = FACTORIES.get(constantClass);
            if (Objects.nonNull(factory)) {
                return factory;
            }
            factory = new DictionaryDynamicConstantFactory(constantClass);
            FACTORIES.put(constantClass, factory);
            return factory;
        }
    }

    @SuppressWarnings("unchecked")
    static <C extends DynamicConstant<T>, T> C parse(final Class<C> constantClass, final T code) {
        try {
            final DynamicConstantFactory factory = getFactory(constantClass);
            return (C) factory.getConstantOfCode(code);
        } catch (Exception e) {
            log.error(String.format("parse error constantClass=%s code=%s",
                    constantClass.getSimpleName(), JsonUtil.toJson(code)), e);
            throw BaseExceptionCode.DYNAMIC_CONSTANT_PARSE_ERROR
                    .exception(e, constantClass.getSimpleName(), JsonUtil.toJson(code));
        }
    }

    @SuppressWarnings("unchecked")
    static <C extends DynamicConstant<T>, T> C parse(final Class<C> constantClass,
                                                     final T code,
                                                     final String name,
                                                     final Object... properties) {
        try {
            final DynamicConstantFactory factory = getFactory(constantClass);
            final C constant = (C) factory.getConstantOfCode(code);
            if (Objects.nonNull(constant)) {
                return constant;
            }
            final DynamicConstantDefinition definition =
                    createDefinition(constantClass, code, name, properties);
            factory.getRegistry().registerDefinition(name, definition);
            return (C) factory.getConstantOfCode(code);
        } catch (Exception e) {
            log.error(String.format("parse error constantClass=%s code=%s name=%s properties=%s",
                    constantClass.getSimpleName(), JsonUtil.toJson(code),
                    name, JsonUtil.toJson(properties)), e);
            throw BaseExceptionCode.DYNAMIC_CONSTANT_PARSE_ERROR
                    .exception(e, constantClass.getSimpleName(), JsonUtil.toJson(code));
        }
    }

    @SuppressWarnings("unchecked")
    static <C extends DynamicConstant<T>, T> List<C> values(final Class<C> constantClass) {
        return (List<C>) getFactory(constantClass).getAllConstant();
    }

    private static <C extends DynamicConstant<T>, T> DynamicConstantDefinition createDefinition(
            final Class<C> constantClass,
            final T code,
            final String name,
            final Object... properties) {
        final Map<String, String> map = new HashMap<>();
        map.put(CODE_FIELD_NAME, JsonUtil.toJson(code));
        final List<Field> fields = Arrays.stream(constantClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .collect(Collectors.toList());
        for (int i = 0; i < fields.size(); i++) {
            map.put(fields.get(i).getName(), JsonUtil.toJson(properties[i]));
        }
        return new DynamicConstantDefinition(name, constantClass.getName(), map);
    }
}
