package com.liquido.statement.service.payment.convert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.statement.pojo.dto.payment.BasePayoutResult;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;


@Slf4j
@Configuration
public class PayoutResultConvertFactory {

    private static final Map<ProductCodeEnum,
            Pair<PayoutResultConvert<? extends BasePayoutResult>, Class<?>>>
            providerMap = Maps.newConcurrentMap();

    public PayoutResultConvertFactory(
            final ObjectProvider<List<PayoutResultConvert>> configurationProvider) {

        final List<PayoutResultConvert> providerList = configurationProvider.getIfAvailable();
        if (!CollectionUtils.isEmpty(providerList)) {
            for (final PayoutResultConvert<? extends BasePayoutResult> strategy : providerList) {
                Class<?> clazz = getInterfaceT(strategy, 0);
                providerMap.put(strategy.getProductCode(), Pair.of(strategy, clazz));
            }
        }
    }

    public Pair<PayoutResultConvert<?
            extends BasePayoutResult>, Class<?>> getConvertProvider(ProductCodeEnum code) {
        if (code == null || providerMap.get(code) == null) {
            return null;
        }

        return providerMap.get(code);
    }

    private static Class<?> getInterfaceT(Object obj, int index) {
        final Type[] types = AopUtils.isAopProxy(obj)
                ? obj.getClass().getSuperclass().getGenericInterfaces()
                : obj.getClass().getGenericInterfaces();

        final ParameterizedType parameterizedType = (ParameterizedType) types[index];
        final Type type = parameterizedType.getActualTypeArguments()[index];
        return checkType(type, index);

    }

    private static Class<?> checkType(final Type type, final int index) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) type;
            final Type t = pt.getActualTypeArguments()[index];
            return checkType(t, index);
        }

        throw new IllegalArgumentException(
                "Expected a Class, ParameterizedType, but <" + type + "> is of type "
                        + (Objects.isNull(type) ? "null" : type.getClass().getName()));
    }
}
