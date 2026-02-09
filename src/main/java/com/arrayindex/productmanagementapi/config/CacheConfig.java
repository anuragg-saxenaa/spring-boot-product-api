package com.arrayindex.productmanagementapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        try {
            // Test Redis connection
            redisConnectionFactory.getConnection().ping();
            log.info("Redis connection successful, using Redis cache manager");
            
            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
            
            // Configure different cache TTLs
            cacheConfigurations.put("products", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .disableCachingNullValues());
            
            cacheConfigurations.put("productById", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(30))
                    .disableCachingNullValues());
            
            cacheConfigurations.put("productCategories", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(60))
                    .disableCachingNullValues());

            return RedisCacheManager.builder(redisConnectionFactory)
                    .withInitialCacheConfigurations(cacheConfigurations)
                    .build();
                    
        } catch (Exception e) {
            log.warn("Redis connection failed, falling back to simple cache manager: {}", e.getMessage());
            return simpleCacheManager();
        }
    }

    @Bean
    public ConcurrentMapCacheManager simpleCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}