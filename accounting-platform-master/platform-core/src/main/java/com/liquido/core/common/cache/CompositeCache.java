package com.liquido.core.common.cache;


import java.util.Objects;

public class CompositeCache extends AbstractCache {

    private final AbstractCache highPriorityCache;

    private final AbstractCache lowPriorityCache;

    public CompositeCache(final String name,
                          final AbstractCache highPriorityCache,
                          final AbstractCache lowPriorityCache) {
        super(name);
        this.highPriorityCache = highPriorityCache;
        this.lowPriorityCache = lowPriorityCache;
    }

    @Override
    protected void doEvict(final Object key) {
        lowPriorityCache.doEvict(key);
        highPriorityCache.doEvict(key);
    }

    @Override
    protected void doPut(final Object key, final Object value) {
        lowPriorityCache.doPut(key, value);
        highPriorityCache.doPut(key, value);
    }

    @Override
    protected Object doGet(final Object key) {
        Object value = highPriorityCache.doGet(key);
        if (Objects.nonNull(value)) {
            return value;
        }
        value = lowPriorityCache.doGet(key);
        if (Objects.nonNull(value)) {
            highPriorityCache.doPut(key, value);
        }
        return value;
    }

    @Override
    public void clear() {
        lowPriorityCache.clear();
        highPriorityCache.clear();
    }
}
