package com.liquido.core.common.cache;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.liquido.core.common.exception.MvcExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis DistLock
 * 1. Mutual exclusion. At any time, only one client can hold the lock.
 * 2. No deadlock occurs. Even if a client crashes while holding the lock and does not actively
 * unlock it, other clients can be guaranteed to lock in the future.
 * 3. It is fault-tolerant. Clients can lock and unlock as long as most of the Redis nodes are
 * up and running.
 * 4. The person who unlocks the bell must also be tied to the bell. Locking and unlocking must be
 * done by the same client, and the client cannot unlock the locks added by others.
 */
@Slf4j
@RequiredArgsConstructor
public class RedisDistLock {

    private static int DEFAULT_RETRY_TIMES = 20;
    private static int MAX_RETRY_TIMES = 200;
    private static long MAX_RETRY_INTERVAL = 60000;

    private final RedisTemplate redisTemplate;

    /**
     * redis unlock LUA script
     * redis doc: https://redis.io/topics/distlock
     */

    private static final String LOCK_SCRIPT = "if redis.call('SETNX', KEYS[1], ARGV[1]) == 1 then "
            + "redis.call('PEXPIRE', KEYS[1], ARGV[2]) return 1 else return 0 end";


    @SuppressWarnings("checkstyle:OperatorWrap")
    public static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] "
            + "then return redis.call('del', KEYS[1]) else return 0 end";

    public static final String LUA_INCR =
            "if redis.call('incr', KEYS[1]) > tonumber(ARGV[1]) "
                    + "then redis.call('decr', KEYS[1]) return 0 else return 1 end";

    public static final String LUA_DECR =
            "if redis.call('decr', KEYS[1]) < 0 "
                    + "then redis.call('set',KEYS[1],0) return 0 else return 1 end";

    /**
     * tryLock
     * retry-times: 10
     *
     * @param lockKey lockLey
     * @param value   lock value, a global unique request ID, such as: UUID
     * @param timeout expire time(milliseconds)
     *
     * @return boolean true: success, false: fail
     */
    public boolean tryLock(final String lockKey, final String value, final long timeout) {
        return this.tryLock(lockKey, value, timeout,
                TimeUnit.MILLISECONDS,
                DEFAULT_RETRY_TIMES,
                RandomUtils.nextInt(20, 100));
    }

    /**
     * tryLock
     * max-retry-times: 1000
     *
     * @param lockKey    lockLey
     * @param value      lock value, a global unique request ID, such as: UUID
     * @param timeout    expire time(milliseconds)
     * @param retryTimes retry times  max-value: 200
     *
     * @return boolean true: success, false: fail
     */
    public boolean tryLock(final String lockKey, final String value, final long timeout,
                           final int retryTimes) {
        return this.tryLock(lockKey, value, timeout,
                TimeUnit.MILLISECONDS,
                retryTimes,
                RandomUtils.nextInt(20, 100));
    }

    /**
     * tryLock
     * retry-times: 10
     *
     * @param lockKey  lockLey
     * @param value    lock value, a global unique request ID, such as: UUID
     * @param timeout  expire time retry times  max-value: 200
     * @param timeUnit expire timeUnit
     *
     * @return boolean true: success, false: fail
     */
    public boolean tryLock(final String lockKey, final String value,
                           final long timeout,
                           final TimeUnit timeUnit) {
        return this.tryLock(lockKey, value, timeout, timeUnit,
                DEFAULT_RETRY_TIMES,
                RandomUtils.nextInt(20, 100));
    }

    /**
     * tryLock
     *
     * @param lockKey    lockLey
     * @param value      lock value, a global unique request ID, such as: UUID
     * @param timeout    expire time
     * @param timeUnit   expire timeUnit
     * @param retryTimes retry times  max-retry-times: 200
     * @param interval   retry interval(milliseconds), max-interval:60s
     *
     * @return boolean true: success, false: fail
     */
    public boolean tryLock(final String lockKey, final String value,
                           final long timeout, final TimeUnit timeUnit,
                           int retryTimes, long interval) {

        retryTimes = retryTimes > MAX_RETRY_TIMES
                ? MAX_RETRY_TIMES : (retryTimes <= 0 ? DEFAULT_RETRY_TIMES : retryTimes);
        interval = interval > MAX_RETRY_INTERVAL
                ? MAX_RETRY_INTERVAL : (interval <= 0 ? RandomUtils.nextInt(20, 100) : interval);

        for (int i = 0; i < retryTimes; i++) {
            try {
                if (this.getLock(lockKey, value, timeout, timeUnit)) {
                    return true;
                }

                TimeUnit.MILLISECONDS.sleep(interval);
            } catch (InterruptedException e) {
                log.error("tryLock error:{}", e.getMessage(), e);
            }
        }

        return false;
    }


    /**
     * get lock
     *
     * @param lockKey lockLey
     * @param value   lock value, a unique request ID, such as: UUID
     * @param timeout expire time
     *
     * @return boolean true: success, false: fail
     */
    public boolean getLock(final String lockKey, final String value, final long timeout) {
        return this.getLock(lockKey, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * get lock
     *
     * @param lockKey  lockLey
     * @param value    lock value, a unique request ID, such as: UUID
     * @param timeout  expire time
     * @param timeUnit expire timeUnit
     *
     * @return boolean true: success, false: fail
     */
    public boolean getLock(final String lockKey, final String value,
                           final long timeout, final TimeUnit timeUnit) {
        try {
            final RedisSerializer<String> keySerializer =
                    (RedisSerializer<String>) redisTemplate.getKeySerializer();
            final String key = new String(keySerializer.serialize(lockKey), StandardCharsets.UTF_8);
            final RedisCallback<Boolean> callback =
                    (connection) -> connection.set(key.getBytes(StandardCharsets.UTF_8),
                            value.getBytes(StandardCharsets.UTF_8),
                            Expiration.milliseconds(
                                    TimeUnit.MILLISECONDS.convert(timeout, timeUnit)),
                            RedisStringCommands.SetOption.SET_IF_ABSENT);

            final Object state = redisTemplate.execute(callback);
            return Objects.isNull(state) ? false : (boolean) state;
        } catch (Exception e) {
            log.error("redis lock error.", e);
        }

        return false;
    }


    /**
     * get lock
     *
     * @param lockKey  lockLey
     * @param value    lock value, a unique request ID, such as: UUID
     * @param timeout  expire time
     * @param timeUnit expire timeUnit
     *
     * @return boolean true: success, false: fail
     */
    public boolean getLock(final String lockKey, final String value,
                           final Long timeout, final TimeUnit timeUnit) {
        try {

            final RedisSerializer<String> keySerializer = this.redisTemplate.getKeySerializer();
            final String key = new String(keySerializer.serialize(lockKey), StandardCharsets.UTF_8);
            final RedisCallback<Boolean> callback = (connection) -> connection.eval(
                    LOCK_SCRIPT.getBytes(),
                    ReturnType.BOOLEAN,
                    1,
                    key.getBytes(StandardCharsets.UTF_8),
                    value.getBytes(StandardCharsets.UTF_8),
                    String.valueOf(TimeUnit.MILLISECONDS.convert(timeout, timeUnit))
                            .getBytes(StandardCharsets.UTF_8)
            );
            return (Boolean) redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("redis lock error.", e);
        }

        return false;
    }

    /**
     * unlock
     * see redis doc: https://redis.io/topics/distlock
     *
     * @param lockKey lockKey
     * @param value   lock value, a unique request ID, such as: UUID
     *
     * @return boolean true: success, false: fail
     */
    public boolean unlock(final String lockKey, final String value) {
        final RedisSerializer<String> keySerializer = this.redisTemplate.getKeySerializer();
        final String key = new String(keySerializer.serialize(lockKey), StandardCharsets.UTF_8);
        final RedisCallback<Boolean> callback =
                (connection) -> connection.eval(UNLOCK_SCRIPT.getBytes(), ReturnType.BOOLEAN, 1,
                        key.getBytes(StandardCharsets.UTF_8),
                        value.getBytes(StandardCharsets.UTF_8));
        return (Boolean) redisTemplate.execute(callback);
    }

    /**
     * get lock value
     *
     * @param lockKey
     *
     * @return
     */
    public String getValue(final String lockKey) {
        try {
            final RedisSerializer<String> keySerializer = this.redisTemplate.getKeySerializer();
            final String key = new String(keySerializer.serialize(lockKey), StandardCharsets.UTF_8);
            final RedisCallback<String> callback = (connection) -> {
                final byte[] bytes = connection.get(key.getBytes(StandardCharsets.UTF_8));
                return Objects.nonNull(bytes) ? new String(bytes, StandardCharsets.UTF_8) : null;
            };
            return (String) redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("get redis occurred an exception", e);
        }
        return null;
    }


    public boolean incrAllowExecutionRequestNumber(final String key,
                                                   final Integer maxRequestNumber) {
        return this.incrAllowExecutionRequestNumber(key, maxRequestNumber, -1, null);
    }


    public boolean incrAllowExecutionRequestNumber(final String key,
                                                   final Integer maxRequestNumber,
                                                   final Integer expireTime,
                                                   final TimeUnit timeUnit) {
        try {
            final DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(LUA_INCR);
            script.setResultType(Long.class);

            final Long result =
                    (Long) redisTemplate.execute(script, Collections.singletonList(key),
                            maxRequestNumber);

            if (result != null && result == 1L
                    && Objects.nonNull(expireTime) && expireTime > -1
                    && Objects.nonNull(timeUnit)) {
                redisTemplate.expire(key, expireTime, timeUnit);
            }

            return result != null && result == 1;
        } catch (Exception e) {
            log.error("incr allow execution request number fail", e);
        }
        return false;
    }


    public boolean decrAllowExecutionRequestNumber(final String key) {
        try {
            final DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(LUA_DECR);
            script.setResultType(Long.class);

            final Long result =
                    (Long) redisTemplate.execute(script, Collections.singletonList(key));
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("decr allow execution request number fail", e);
        }
        return false;
    }

    public void checkRepeatRequest(final String businessKey, final String requestId,
                                   final long timeout, final TimeUnit timeUnit) {
        if (!this.getLock(businessKey + requestId, requestId, timeout, timeUnit)) {
            throw MvcExceptionCode.REPEAT_SUBMIT_ERROR.exception();
        }
    }
}

