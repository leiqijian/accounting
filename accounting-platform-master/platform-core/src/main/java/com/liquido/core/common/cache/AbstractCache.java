package com.liquido.core.common.cache;

import java.util.Objects;
import java.util.concurrent.Callable;

import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.support.SimpleValueWrapper;

public abstract class AbstractCache implements Cache {

    protected final String name;

    public AbstractCache(final String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final Object getNativeCache() {
        return this;
    }

    @Override
    public final ValueWrapper get(final Object key) {
        Object value = doGet(key);
        return Objects.nonNull(value) ? new SimpleValueWrapper(value) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T get(final Object key, final Class<T> type) {
        final Object value = doGet(key);
        if (Objects.nonNull(value) && Objects.nonNull(type) && !type.isInstance(value)) {
            throw new IllegalStateException(
                    "Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T get(final Object key, final Callable<T> valueLoader) {
        final ValueWrapper result = get(key);
        if (Objects.nonNull(result)) {
            return (T) result.get();
        }
        return getSynchronized(key, valueLoader);
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> T getSynchronized(final Object key, final Callable<T> valueLoader) {
        final ValueWrapper result = get(key);
        if (Objects.nonNull(result)) {
            return (T) result.get();
        }
        final T value;
        try {
            value = valueLoader.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
        put(key, value);
        return value;
    }

    @Override
    public final void put(final Object key, final Object value) {
        if (!unless(value)) {
            doPut(key, value);
        }
    }

    @Override
    public final void evict(final Object key) {
        doEvict(key);
    }

    protected abstract void doEvict(final Object key);

    protected abstract void doPut(final Object key, final Object value);

    protected abstract Object doGet(final Object key);

    /**
     * default unless {@link Cacheable#unless()} {@link CachePut#unless()}
     */
    private boolean unless(final Object value) {
        if (Objects.isNull(value)) {
            return true;
        }
        if (value instanceof ResponseDto) {
            final ResponseDto<?> vo = (ResponseDto<?>) value;
            return vo.isFail() || Objects.isNull(vo.getData());
        }
        return false;
    }
}
