package com.liquido.core.common.cache;

import java.util.concurrent.TimeUnit;

public class RedisExpiryCache extends AbstractExpiryCache {

    private final RedisCacheUtil cache;

    protected RedisExpiryCache(final String name, final RedisCacheUtil cache, final int expiry) {
        super(name, expiry);
        this.cache = cache;
    }

    @Override
    protected void doPut(final Object key, final Object value) {
        if (isEternalValue()) {
            cache.setCacheObject((String) key, value);
        } else {
            cache.setCacheObject((String) key, value, expiry, TimeUnit.SECONDS);
        }
    }

    @Override
    protected Object doGet(final Object key) {
        return cache.getCacheObject((String) key);
    }

    @Override
    public void doEvict(final Object key) {
        cache.deleteObject((String) key);
    }

    @Override
    public void clear() {
    }
}
