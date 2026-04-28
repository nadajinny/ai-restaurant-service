package com.restaurant.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.backend.common.cache.CacheNames;
import com.restaurant.backend.common.cache.ResilientCacheManager;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager(
            ObjectProvider<RedisConnectionFactory> redisConnectionFactoryProvider,
            ObjectMapper objectMapper,
            @Value("${app.cache.enabled:true}") boolean cacheEnabled,
            @Value("${app.cache.redis-enabled:true}") boolean redisEnabled
    ) {
        if (!cacheEnabled) {
            log.info("Application cache is disabled.");
            return new NoOpCacheManager();
        }

        ConcurrentMapCacheManager fallbackCacheManager = createFallbackCacheManager();

        if (!redisEnabled) {
            log.info("Redis cache is disabled. Using in-memory cache only.");
            return fallbackCacheManager;
        }

        RedisConnectionFactory redisConnectionFactory = redisConnectionFactoryProvider.getIfAvailable();
        if (redisConnectionFactory == null) {
            log.warn("RedisConnectionFactory is not available. Falling back to in-memory cache.");
            return fallbackCacheManager;
        }

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultRedisConfiguration(objectMapper))
                .withInitialCacheConfigurations(cacheConfigurations(objectMapper))
                .transactionAware()
                .build();

        return new ResilientCacheManager(redisCacheManager, fallbackCacheManager);
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Cache GET error. cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                log.warn("Cache PUT error. cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Cache EVICT error. cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                log.warn("Cache CLEAR error. cache={}", cache.getName(), exception);
            }
        };
    }

    private ConcurrentMapCacheManager createFallbackCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(CacheNames.ALL.toArray(String[]::new));
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    private Map<String, RedisCacheConfiguration> cacheConfigurations(ObjectMapper objectMapper) {
        return Map.of(
                CacheNames.MENUS, redisConfiguration(Duration.ofMinutes(10), objectMapper),
                CacheNames.MENU_DETAILS, redisConfiguration(Duration.ofMinutes(10), objectMapper),
                CacheNames.POPULAR_MENUS, redisConfiguration(Duration.ofMinutes(30), objectMapper),
                CacheNames.REVIEW_SUMMARIES, redisConfiguration(Duration.ofMinutes(30), objectMapper),
                CacheNames.PERSONALIZED_RECOMMENDATIONS, redisConfiguration(Duration.ofHours(1), objectMapper),
                CacheNames.ADMIN_DASHBOARD, redisConfiguration(Duration.ofMinutes(1), objectMapper),
                CacheNames.SALES_ANALYTICS, redisConfiguration(Duration.ofMinutes(5), objectMapper),
                CacheNames.MENU_PERFORMANCE, redisConfiguration(Duration.ofMinutes(5), objectMapper),
                CacheNames.HOURLY_ORDERS, redisConfiguration(Duration.ofMinutes(5), objectMapper)
        );
    }

    private RedisCacheConfiguration defaultRedisConfiguration(ObjectMapper objectMapper) {
        return redisConfiguration(Duration.ofMinutes(10), objectMapper);
    }

    private RedisCacheConfiguration redisConfiguration(Duration ttl, ObjectMapper objectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(objectMapper.copy())
                ));
    }
}
