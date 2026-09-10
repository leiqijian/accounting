package com.liquido.core.common.cache;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

public class ExpiryCacheManager extends AbstractCacheManager {

    private static final Pattern REDIS_CACHE_PATTERN =
            Pattern.compile("REDIS:(-1|[1-9][0-9]*|[1-9][0-9]*(d|h|min|s))");

    private static final Pattern LOCAL_CACHE_PATTERN =
            Pattern.compile("LOCAL:(-1|[1-9][0-9]*|[1-9][0-9]*(d|h|min|s))");

    private static final Pattern LOCAL_REDIS_CACHE_PATTERN =
            Pattern.compile("LOCAL:(-1|[1-9][0-9]*|[1-9][0-9]*(d|h|min|s))"
                    + "#REDIS:(-1|[1-9][0-9]*|[1-9][0-9]*(d|h|min|s))");

    private final RedisCacheUtil redisCacheUtil;

    private final int maximumSize;

    public ExpiryCacheManager(final RedisCacheUtil redisCacheUtil, final int maximumSize) {
        this.redisCacheUtil = redisCacheUtil;
        this.maximumSize = maximumSize;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return List.of();
    }

    @Override
    protected Cache getMissingCache(final String name) {
        if (REDIS_CACHE_PATTERN.matcher(name).matches()) {
            return new RedisExpiryCache(name, redisCacheUtil, expiry(name));
        }

        if (LOCAL_CACHE_PATTERN.matcher(name).matches()) {
            return new LocalExpiryCache(name, maximumSize, expiry(name));
        }

        if (LOCAL_REDIS_CACHE_PATTERN.matcher(name).matches()) {
            final String[] split = name.split("#");
            final LocalExpiryCache localCache =
                    new LocalExpiryCache(split[0], maximumSize, expiry(split[0]));
            final RedisExpiryCache redisCache =
                    new RedisExpiryCache(split[1], redisCacheUtil, expiry(split[1]));
            return new CompositeCache(name, localCache, redisCache);
        }

        return null;
    }

    private int expiry(final String name) {
        String desc = extractExpiryDescription(name);
        if (desc.endsWith("s")) {
            return (int) TimeUnit.SECONDS.toSeconds(Integer.parseInt(desc.replace("s", "")));
        }
        if (desc.endsWith("min")) {
            return (int) TimeUnit.MINUTES.toSeconds(Integer.parseInt(desc.replace("min", "")));
        }
        if (desc.endsWith("h")) {
            return (int) TimeUnit.HOURS.toSeconds(Integer.parseInt(desc.replace("h", "")));
        }
        if (desc.endsWith("d")) {
            return (int) TimeUnit.DAYS.toSeconds(Integer.parseInt(desc.replace("d", "")));
        }
        if (desc.equals("-1")) {
            return -1;
        }
        return Integer.parseInt(desc);
    }

    private String extractExpiryDescription(final String name) {
        return name.split(":")[1];
    }
}
