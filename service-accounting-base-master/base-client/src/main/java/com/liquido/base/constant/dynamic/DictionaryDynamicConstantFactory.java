package com.liquido.base.constant.dynamic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.liquido.core.common.utils.JsonUtil;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class DictionaryDynamicConstantFactory implements DynamicConstantFactory {

    private final DynamicConstantDefinitionRegistry registry;

    private final Map<String, DynamicConstant<?>> constants;

    private final Map<Object, String> nameByCode;

    public DictionaryDynamicConstantFactory(
            final Class<? extends DynamicConstant<?>> constantClass) {
        this.registry = new DictionaryDynamicConstantDefinitionRegistry(constantClass);
        this.constants = new ConcurrentHashMap<>();
        this.nameByCode = new ConcurrentHashMap<>();
    }

    @Override
    public DynamicConstant<?> getConstantOfCode(final Object code) {
        final String name = getNameForCode(code);
        if (Objects.isNull(name)) {
            return null;
        }
        return getConstant(name);
    }

    @Override
    public DynamicConstant<?> getConstant(final String name) {
        DynamicConstant<?> constant = constants.get(name);
        if (Objects.nonNull(constant)) {
            return constant;
        }
        synchronized (constants) {
            constant = constants.get(name);
            if (Objects.nonNull(constant)) {
                return constant;
            }
            final DynamicConstantDefinition definition = registry.getDefinition(name);
            if (Objects.isNull(definition)) {
                return null;
            }
            constant = createConstant(definition);
            constants.put(name, constant);
            return constant;
        }
    }

    private String getNameForCode(final Object code) {
        String name = nameByCode.get(code);
        if (Objects.nonNull(name)) {
            return name;
        }
        name = registry.getDefinitionNames()
                .stream()
                .filter(e -> isCodeMatch(e, code))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(name)) {
            return null;
        }
        nameByCode.put(code, name);
        return name;
    }

    private boolean isCodeMatch(final String name, final Object code) {
        final DynamicConstant<?> constant = getConstant(name);
        if (Objects.isNull(constant)) {
            return false;
        }
        return Objects.equals(constant.getCode(), code);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private DynamicConstant<?> createConstant(final DynamicConstantDefinition definition) {
        log.info("createConstant definition={}", definition);
        final Class<DynamicConstant<?>> constantClass = (Class<DynamicConstant<?>>) Class
                .forName(definition.getClassName());

        DynamicConstant<?> constant = Arrays.stream(constantClass.getDeclaredFields())
                .filter(ReflectionUtils::isPublicStaticFinal)
                .filter(field -> field.getType().equals(constantClass))
                .filter(field -> field.getName().equals(definition.getName()))
                .map(field -> constantClass.cast(readStaticField(field)))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(constant)) {
            return constant;
        }

        final Constructor<?> constructor = sun.misc.Unsafe.class.getDeclaredConstructors()[0];
        ReflectionUtils.makeAccessible(constructor);
        final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) constructor.newInstance();
        constant = (DynamicConstant<?>) unsafe.allocateInstance(constantClass);
        final Map<String, String> properties = definition.getProperties();
        for (final Map.Entry<String, String> entry : properties.entrySet()) {
            final Field field = ReflectionUtils.findField(constantClass, entry.getKey());
            ReflectionUtils.makeAccessible(Objects.requireNonNull(field));
            final Class<?> type = field.getDeclaringClass().equals(DynamicConstant.class)
                    ? GenericTypeResolver.resolveTypeArgument(constantClass, DynamicConstant.class)
                    : field.getType();
            field.set(constant, JsonUtil.toBean(entry.getValue(), type));
        }
        return constant;
    }

    private Object readStaticField(final Field field) {
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DynamicConstant<?>> getAllConstant() {
        return registry.getDefinitionNames()
                .stream()
                .map(this::getConstant)
                .collect(Collectors.toList());
    }

    @Override
    public DynamicConstantDefinitionRegistry getRegistry() {
        return registry;
    }
}
