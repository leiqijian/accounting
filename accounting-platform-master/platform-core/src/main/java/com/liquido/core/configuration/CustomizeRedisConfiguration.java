package com.liquido.core.configuration;

import javax.annotation.Resource;

import com.liquido.core.common.cache.CacheStringSerializer;
import com.liquido.core.common.cache.ExpiryCacheManager;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.cache.RedisRateLimitUtil;
import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Redis Configuration
 */
@Slf4j
@EnableCaching
@Configuration
@ConditionalOnProperty(name = "ms.common.cache-service.enable", havingValue = "true")
public class CustomizeRedisConfiguration extends CachingConfigurerSupport {

    @Value("${spring.cache.redis.key-prefix:}")
    private String prefix;
    @Value("${spring.cache.redis.key-max-length:64}")
    private Integer maxKeyLength;
    @Value("${spring.redis.host:localhost}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private Integer port;
    @Value("${spring.redis.database:0}")
    private Integer database;
    @Value("${spring.redis.username:}")
    private String username;
    @Value("${spring.redis.password:}")
    private String password;

    @Resource
    private CommonProperties commonProperties;

    @Bean
    public LettuceConnectionFactory jedisConnectionFactory() {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(database);
        if (Strings.isNotBlank(username)) {
            config.setUsername(username);
        }

        if (Strings.isNotBlank(password)) {
            config.setPassword(password);
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public CacheStringSerializer stringSerializer() {
        return new CacheStringSerializer(prefix, maxKeyLength);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            final LettuceConnectionFactory jedisConnectionFactory) {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setKeySerializer(stringSerializer());
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);

        return stringRedisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            final LettuceConnectionFactory jedisConnectionFactory) {

        final Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer(Object.class);

        final ObjectMapper objectMapper = JsonUtil.getObjectMapper().copy();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.WRAPPER_ARRAY);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);


        // config redisTemplate
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        // RedisSerializer stringSerializer = new StringRedisSerializer();
        // key Serializer
        redisTemplate.setKeySerializer(stringSerializer());
        // value Serializer
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        // Hash key Serializer
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);
        // Hash value Serializer
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            final StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());

            for (final Object obj : params) {
                sb.append(":" + obj);
            }

            final String rsToUse = String.valueOf(sb);

            log.info("auto generic redis key -> [{}]", rsToUse);
            return prefix + "-" + rsToUse;
        };
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        log.info("Init -> [{}]", "Redis CacheErrorHandler");
        final CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key,
                    Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value,
                        e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }
        };
        return cacheErrorHandler;
    }

    @Bean
    public RedisCacheUtil redisCacheUtil(
            final RedisTemplate redisTemplate,
            final StringRedisTemplate stringRedisTemplate) {

        return new RedisCacheUtil(redisTemplate, stringRedisTemplate, prefix);
    }

    @Bean
    public RedisRateLimitUtil redisRateLimitUtil(
            final StringRedisTemplate stringRedisTemplate) {

        return new RedisRateLimitUtil(stringRedisTemplate);
    }

    @Bean
    public CacheManager cacheManager(
            final RedisCacheUtil redisCacheUtil) {

        return new ExpiryCacheManager(redisCacheUtil,
                commonProperties.getCacheService().getCaffeine().getMaximumSize());
    }

    @Bean
    public RedisDistLock redisLock(final RedisTemplate redisTemplate) {
        return new RedisDistLock(redisTemplate);
    }
}
