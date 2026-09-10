package com.liquido.core.common.cache;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class LocalExpiryCache extends AbstractExpiryCache {

    private final Cache<Object, Object> cache;

    protected LocalExpiryCache(final String name, final int maximumSize, final int expiry) {
        super(name, expiry);
        final Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if (!isEternalValue()) {
            builder.expireAfterWrite(expiry, TimeUnit.SECONDS);
        }
        cache = builder.maximumSize(maximumSize).build();
    }

    @Override
    protected void doPut(final Object key, final Object value) {
        cache.put(key, value);
    }

    @Override
    protected Object doGet(final Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void doEvict(final Object key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }
}
