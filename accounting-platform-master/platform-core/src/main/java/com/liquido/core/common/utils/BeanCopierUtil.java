package com.liquido.core.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

public class BeanCopierUtil {

    private static final Map<CopierIdentity, BeanCopier> BEAN_COPIER_CACHE =
            new ConcurrentHashMap<>();

    public static void copyProperties(final Object srcObj,
                                      final Object destObj) {

        if (Objects.isNull(srcObj) || Objects.isNull(destObj)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        getCopier(srcObj.getClass(), destObj.getClass(), false)
                .copy(srcObj, destObj, null);
    }

    public static void copyProperties(final Object srcObj,
                                      final Object destObj,
                                      final Converter converter) {

        if (Objects.isNull(srcObj) || Objects.isNull(destObj)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        getCopier(srcObj.getClass(), destObj.getClass(), true)
                .copy(srcObj, destObj, converter);
    }

    public static <T> T copyProperties(final Object srcObj,
                                       final Class<T> destClass) {

        if (Objects.isNull(srcObj) || Objects.isNull(destClass)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        try {
            final T t = destClass.getDeclaredConstructor().newInstance();
            getCopier(srcObj.getClass(), destClass, false)
                    .copy(srcObj, t, null);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T copyProperties(final Object srcObj,
                                       final Class<T> destClass,
                                       final Converter converter) {

        if (Objects.isNull(srcObj) || Objects.isNull(destClass)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        try {
            final T t = destClass.getDeclaredConstructor().newInstance();
            getCopier(srcObj.getClass(), destClass, true)
                    .copy(srcObj, t, converter);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * BeanCopier copy list object
     *
     * @param sources
     * @param target
     */
    public static <S, T> List<T> copyPropertyList(final List<S> sources,
                                                  final Class<T> target) {
        final List<T> list = new ArrayList<>(sources.size());
        for (final S source : sources) {
            list.add(copyProperties(source, target));
        }

        return list;
    }

    /**
     * BeanCopier copy list object
     *
     * @param sources
     * @param target
     */
    public static <S, T> List<T> copyPropertyList(final List<S> sources,
                                                  final Class<T> target,
                                                  final Converter converter) {
        final List<T> list = new ArrayList<>(sources.size());
        for (final S source : sources) {
            list.add(copyProperties(source, target, converter));
        }

        return list;
    }


    /**
     * BeanCopier copy list object
     *
     * @param sources
     * @param target
     */
    public static <S, T> List<T> copyPropertyList(final List<S> sources,
                                                  final Supplier<T> target) {
        final List<T> list = new ArrayList<>(sources.size());
        for (final S source : sources) {
            final T t = target.get();
            copyProperties(source, t);
            list.add(t);
        }

        return list;
    }

    /**
     * BeanCopier copy list object
     *
     * @param sources
     * @param target
     */
    public static <S, T> List<T> copyPropertyList(final List<S> sources,
                                                  final Supplier<T> target,
                                                  final Converter converter) {
        final List<T> list = new ArrayList<>(sources.size());
        for (final S source : sources) {
            final T t = target.get();
            copyProperties(source, t, converter);
            list.add(t);
        }

        return list;
    }

    private static BeanCopier getCopier(final Class<?> source,
                                        final Class<?> target,
                                        final boolean converter) {

        final CopierIdentity key = new CopierIdentity(source, target);
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            return BEAN_COPIER_CACHE.get(key);
        }

        synchronized (BeanCopierUtil.class) {
            if (BEAN_COPIER_CACHE.containsKey(key)) {
                return BEAN_COPIER_CACHE.get(key);
            }
            return BeanCopier.create(source, target, converter);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CopierIdentity {
        private Class<?> source;
        private Class<?> target;
    }
}
