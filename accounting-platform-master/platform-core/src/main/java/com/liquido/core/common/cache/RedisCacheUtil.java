package com.liquido.core.common.cache;


import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
public class RedisCacheUtil {

    // The number of records per scan.
    // The smaller the value, the more scan times and the more time consuming.
    // It is recommended to set it at 1000~10000
    private static final int DEFAULT_SCAN_COUNT = 1000;
    private static final int DEFAULT_SCAN_MAX_COUNT = 10000;

    private final RedisTemplate redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final String keyPrefix;
    private static final String DELIMITER = ".";

    public boolean exists(final String key) {
        Assert.hasText(key, "cache key must not be blank");

        return redisTemplate.hasKey(key);
    }

    /**
     * @param key   key
     * @param value value
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * @param key      key
     * @param value    value
     * @param timeout  timeout
     * @param timeUnit timeUnit
     */
    public <T> void setCacheObject(
            final String key,
            final T value,
            final Integer timeout,
            final TimeUnit timeUnit) {

        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * @param key   key
     * @param value value
     * @param ttl   Duration ttl
     */
    public <T> void setCacheObject(
            final String key,
            final T value,
            final Duration ttl) {

        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * @param key   key
     * @param value value
     * @param ttl   Duration ttl
     */
    public <T> boolean setCacheObjectIfAbsent(
            final String key,
            final T value,
            final Duration ttl) {

        return redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
    }

    public <T> void getAndSetObject(
            final String key,
            final T value) {

        redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * @param key     key
     * @param timeout timeout
     *
     * @return
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * @param key     key
     * @param timeout timeout
     * @param unit    unit
     *
     * @return
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * @param key      key
     * @param dateTime timeout
     *
     * @return
     */
    public boolean expireAt(final String key, final Date dateTime) {
        return redisTemplate.expireAt(key, dateTime);
    }

    /**
     * @param key       key
     * @param localDate timeout
     *
     * @return
     */
    public boolean expireAt(final String key, final LocalDate localDate) {
        return redisTemplate.expireAt(key, LocalDateUtil.convertToDate(localDate));
    }

    /**
     * @param key      key
     * @param dateTime timeout
     *
     * @return
     */
    public boolean expireAt(final String key, final LocalDateTime dateTime) {
        return redisTemplate.expireAt(key,
                Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * @param key      key
     * @param dateTime timeout
     *
     * @return
     */
    public boolean expireAtUtc(final String key, final LocalDateTime dateTime) {
        return redisTemplate.expireAt(key,
                Date.from(dateTime.atZone(ZoneId.of("UTC")).toInstant()));
    }

    /**
     * Get expiration time
     *
     * @param key key
     *
     * @return long TimeUnit: milliseconds
     */
    public long getExpire(final String key) {
        return getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * Get expiration time
     *
     * @param key  key
     * @param unit TimeUnit
     *
     * @return long TimeUnit: milliseconds
     */
    public long getExpire(final String key, final TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * @param key key
     *
     * @return
     */
    public <T> T getCacheObject(final String key) {
        final ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    public <T> T getCacheObject(final String key, final TypeReference<T> typeRef) {

        Assert.notNull(key, "key must not be null");
        Assert.notNull(typeRef, "typeRef must not be null");

        final Object result = redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] rawKey = redisTemplate.getKeySerializer().serialize(key);
            byte[] rawValue = connection.get(rawKey);

            if (rawValue == null) {
                return null;
            }

            try {
                final ObjectMapper mapper = JsonUtil.getObjectMapper();
                final JavaType javaType = mapper.getTypeFactory().constructType(typeRef);
                final Class<?> rawClass = javaType.getRawClass();

                // 1 String
                if (rawClass == String.class) {
                    return new String(rawValue, StandardCharsets.UTF_8);
                }

                final String text = new String(rawValue, StandardCharsets.UTF_8);

                // 2 Primitive wrappers
                if (rawClass == Integer.class) {
                    return Integer.valueOf(text);
                }
                if (rawClass == Long.class) {
                    return Long.valueOf(text);
                }
                if (rawClass == Boolean.class) {
                    return Boolean.valueOf(text);
                }
                if (rawClass == Double.class) {
                    return Double.valueOf(text);
                }
                if (rawClass == BigDecimal.class) {
                    return new BigDecimal(text);
                }

                // 3 JSON / POJO
                return mapper.readValue(rawValue, javaType);

            } catch (Exception e) {
                throw new IllegalStateException("Redis raw get failed, key=" + key, e);
            }
        });

        return result == null ? null : (T) result;
    }


    /**
     * @param key key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * @param collection collection
     *
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * @param key      key
     * @param dataList dataList
     *
     * @return
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * @param key key
     *
     * @return
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * @param key     key
     * @param dataSet dataSet
     *
     * @return
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {

        final BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        for (final T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * @param key key
     *
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public <K, V> void setHash(
            final String key,
            final K mapKey,
            final V value,
            final Duration ttl) {

        // clear if exists before set
        redisTemplate.delete(key);

        redisTemplate.opsForHash().put(key, mapKey, value);
        if (Objects.nonNull(ttl)) {
            redisTemplate.expire(key, ttl);
        }
    }

    /**
     * @param key     key
     * @param dataMap dataMap
     */
    public <K, V> void setCacheMap(final String key, final Map<K, V> dataMap) {
        if (!MapUtils.isEmpty(dataMap)) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * @param key key
     *
     * @return
     */
    public <K, V> Map<K, V> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * @param key     key
     * @param hashKey hashKey
     * @param value   value
     */
    public <K, V> void setCacheMapValue(final String key, final K hashKey, final V value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * @param key     key
     * @param hashKey hashKey
     *
     * @return
     */
    public <K, V> V getCacheMapValue(final String key, final K hashKey) {
        final HashOperations<String, K, V> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hashKey);
    }

    /**
     * @param key     key
     * @param hashKey hashKey
     */
    public <K> void delCacheMapValue(final String key, final K hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * @param key      key
     * @param hashKeys hashKey
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public <K, V> List<V> getMultiCacheMapValue(String key, Collection<K> hashKeys) {
        return (List<V>) redisTemplate.opsForHash().multiGet(key, hashKeys).stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * @param pattern pattern
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }


    /**
     * use redis scan to get cache key
     *
     * @param keyPattern
     * @param count      1000-10000
     */
    public List<String> scan(final String keyPattern, int count) {
        count = count > DEFAULT_SCAN_MAX_COUNT ? DEFAULT_SCAN_MAX_COUNT : count;
        final List<String> keys = new ArrayList<>();
        try (Cursor<Map.Entry<String, Set<String>>> cursor = redisTemplate.opsForHash()
                .scan(keyPattern, ScanOptions.scanOptions().match("*").count(count).build())) {
            while (cursor.hasNext()) {
                String key = cursor.next().getKey();
                keys.add(key);
            }
        }
        return keys;
    }

    /**
     * use redis scan to get cache key
     *
     * @param keyPattern
     * @param count
     *
     * @return
     */
    public List<String> keys(final String keyPattern, int count) {
        count = count > DEFAULT_SCAN_MAX_COUNT ? DEFAULT_SCAN_MAX_COUNT : count;
        final List<String> keys = new ArrayList<>();
        this.scan(keyPattern, count, item -> keys.add(new String(item, StandardCharsets.UTF_8)));

        return keys;
    }

    /**
     * use redis scan to get data
     *
     * @param keyPattern
     * @param consumer
     */
    public void scan(final String keyPattern, int count, Consumer<byte[]> consumer) {
        this.stringRedisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions().count(count).match(keyPattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            }
        });
    }

    /**
     * Generic Eval Script with Class return type
     */
    public <R> R evalScript(
            final String luaScript,
            final ReturnType returnType,
            final List<Object> keys,
            final List<Object> args,
            final Class<R> resultType) {

        Assert.notNull(luaScript, "luaScript must not be null");
        Assert.notNull(returnType, "returnType must not be null");
        Assert.notNull(keys, "keys must not be null");
        Assert.notNull(args, "args must not be null");
        Assert.notNull(resultType, "resultType must not be null");

        try {
            final Object result = doEval(luaScript, returnType, keys, args);

            if (result == null) {
                return null;
            }

            if (resultType == String.class && result instanceof byte[]) {
                return resultType.cast(new String((byte[]) result, StandardCharsets.UTF_8));
            }

            return JsonUtil.getObjectMapper().convertValue(result, resultType);

        } catch (Exception e) {
            log.error("Redis Lua script execution failed, script={}", luaScript, e);
            throw new IllegalStateException("Redis script execution failed", e);
        }
    }

    /**
     * Generic Eval Script with TypeReference return type
     */
    public <R> R evalScript(
            final String luaScript,
            final ReturnType returnType,
            final List<Object> keys,
            final List<Object> args,
            final TypeReference<R> typeReference) {

        Assert.notNull(luaScript, "luaScript must not be null");
        Assert.notNull(typeReference, "typeReference must not be null");

        try {
            final Object raw = doEval(luaScript, returnType, keys, args);
            if (raw == null) {
                return null;
            }

            final ObjectMapper mapper = JsonUtil.getObjectMapper();

            // Redis bulk string
            if (raw instanceof byte[]) {
                return mapper.readValue(
                        new String((byte[]) raw, StandardCharsets.UTF_8),
                        typeReference);
            }

            // Redis array
            if (raw instanceof List<?>) {
                final List<?> list = (List<?>) raw;
                final List<Object> decoded = new ArrayList<>(list.size());

                for (final Object o : list) {
                    if (o instanceof byte[]) {
                        decoded.add(new String((byte[]) o, StandardCharsets.UTF_8));
                    } else {
                        decoded.add(o);
                    }
                }

                return mapper.convertValue(decoded, typeReference);
            }

            // number / map / pojo
            return mapper.convertValue(raw, typeReference);

        } catch (Exception e) {
            log.error("Redis Lua script mapping failed, script={}", luaScript, e);
            throw new IllegalStateException("Redis script mapping failed", e);
        }
    }

    /**
     * 执行 Lua Script
     */
    @SuppressWarnings("unchecked")
    private Object doEval(
            final String luaScript,
            final ReturnType returnType,
            final List<Object> keys,
            final List<Object> args) {

        final byte[][] keyBytes = keys.stream()
                .map(k -> redisTemplate.getKeySerializer().serialize(String.valueOf(k)))
                .toArray(byte[][]::new);

        final byte[][] argBytes = args.stream()
                .map(this::serializeArg)
                .toArray(byte[][]::new);

        final byte[] scriptBytes = luaScript.getBytes(StandardCharsets.UTF_8);

        return redisTemplate.execute((RedisCallback<Object>) connection ->
                connection.eval(
                        scriptBytes,
                        returnType,
                        keyBytes.length,
                        concatBytes(keyBytes, argBytes)
                )
        );
    }

    /**
     * serialize Lua args
     */
    private byte[] serializeArg(final Object arg) {
        if (arg == null) {
            return new byte[0];
        }

        // Number / Boolean / String →  UTF-8
        if (arg instanceof Number || arg instanceof Boolean || arg instanceof String) {
            return String.valueOf(arg).getBytes(StandardCharsets.UTF_8);
        }

        // other beans → JSON serialize
        return redisTemplate.getValueSerializer().serialize(arg);
    }

    private byte[][] concatBytes(final byte[][] keys, final byte[][] args) {
        final byte[][] result = new byte[keys.length + args.length][];
        System.arraycopy(keys, 0, result, 0, keys.length);
        System.arraycopy(args, 0, result, keys.length, args.length);

        return result;
    }
}
