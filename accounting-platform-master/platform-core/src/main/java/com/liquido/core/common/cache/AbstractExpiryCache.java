package com.liquido.core.common.cache;

public abstract class AbstractExpiryCache extends AbstractCache {

    protected final int expiry;

    public AbstractExpiryCache(final String name, final int expiry) {
        super(name);
        this.expiry = expiry;
    }

    protected final boolean isEternalValue() {
        return expiry < 1;
    }
}
