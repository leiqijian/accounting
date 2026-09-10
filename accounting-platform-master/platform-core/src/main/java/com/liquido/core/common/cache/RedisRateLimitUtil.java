package com.liquido.core.common.cache;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class RedisRateLimitUtil {

    // Sliding window time, in seconds
    private static final long WINDOW_SIZE = 60;

    // Maximum number of requests per minute
    private static final long MAX_REQUESTS = 10;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Fixed Window
     *
     * @param requestId
     *
     * @return
     */
    public boolean isFixedWindowRequestAllowed(final String requestId) {
        final String key = "FW_RATE_LIMIT:" + requestId;

        if (Long.valueOf(Optional.ofNullable(
                stringRedisTemplate.opsForValue().get(key)).orElse("0")) > MAX_REQUESTS) {
            return false;
        }
        // 使用 Redis 的 INCR 命令增加计数器
        final Long currentCount = stringRedisTemplate.opsForValue().increment(key, 1);

        if (Optional.ofNullable(currentCount).orElse(0L) == 1) {
            stringRedisTemplate.expire(key, WINDOW_SIZE, TimeUnit.SECONDS);
        }

        if (Objects.nonNull(currentCount) && currentCount > MAX_REQUESTS) {
            return false;
        }

        // 否则允许请求
        return true;
    }

    /**
     * Fixed Window
     *
     * @param requestId
     * @param windowSize
     * @param maxRequests
     *
     * @return
     */
    public boolean isFixedWindowRequestAllowed(final String requestId,
                                               final int windowSize,
                                               final int maxRequests) {
        final String key = "FW_RATE_LIMIT:" + requestId;

        if (Long.valueOf(Optional.ofNullable(
                stringRedisTemplate.opsForValue().get(key)).orElse("0"))
                > (maxRequests > 0 ? maxRequests : MAX_REQUESTS)) {
            return false;
        }

        // 使用 Redis 的 INCR 命令增加计数器
        final Long currentCount = stringRedisTemplate.opsForValue().increment(key, 1);

        if (Optional.ofNullable(currentCount).orElse(0L) == 1) {
            stringRedisTemplate.expire(key, windowSize > 0 ? windowSize : WINDOW_SIZE,
                    TimeUnit.SECONDS);
        }

        if (Objects.nonNull(currentCount)
                && currentCount > (maxRequests > 0 ? maxRequests : MAX_REQUESTS)) {
            return false;
        }

        return true;
    }

    /**
     * SlidingWindow Check if requests exceed the limit
     *
     * @param requestId unique requestId
     *
     * @return True if request is allowed, false if request exceeds frequency limit
     */
    public boolean isSlidingWindowRequestAllowed(final String requestId) {

        final String key = "SW_RATE_LIMIT:" + requestId;
        final long currentTime = System.currentTimeMillis();

        // Use Redis SortdSet to store timestamps
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(currentTime), currentTime);

        // Set expiration time
        stringRedisTemplate.expire(key, WINDOW_SIZE, TimeUnit.SECONDS);

        // Remove expired timestamps (requests exceeding the time window)
        final long windowStart = currentTime - (WINDOW_SIZE * 1000);
        stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // Get the number of requests in the current window
        final Long requestCount = stringRedisTemplate.opsForZSet().size(key);

        // If the number of requests exceeds the limit, return false
        return Objects.isNull(requestCount) || requestCount <= (MAX_REQUESTS);
    }


    /**
     * SlidingWindow Check if requests exceed the limit
     *
     * @param requestId unique requestId
     *
     * @return True if request is allowed, false if request exceeds frequency limit
     */
    public boolean isSlidingWindowRequestAllowed(final String requestId,
                                                 final int windowSize,
                                                 final int maxRequests) {

        final String key = "SW_RATE_LIMIT:" + requestId;
        final long currentTime = System.currentTimeMillis();

        // Use Redis SortdSet to store timestamps
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(currentTime), currentTime);

        // Set expiration time
        stringRedisTemplate.expire(key,
                windowSize > 0 ? windowSize : WINDOW_SIZE, TimeUnit.SECONDS);

        // Remove expired timestamps (requests exceeding the time window)
        final long windowStart = currentTime - (windowSize > 0 ? windowSize : WINDOW_SIZE) * 1000;
        stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // Get the number of requests in the current window
        final Long requestCount = stringRedisTemplate.opsForZSet().size(key);

        // If the number of requests exceeds the limit, return false
        return Objects.isNull(requestCount)
                || requestCount <= (maxRequests > 0 ? maxRequests : MAX_REQUESTS);
    }
}
