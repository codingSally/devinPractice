package com.example.devin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * Configuration for distributed locking
 * This helps improve system availability in distributed environments
 */
@Configuration
public class DistributedLockConfig {

    /**
     * Create a Redis lock registry for distributed locking
     * @param redisConnectionFactory the Redis connection factory
     * @return the Redis lock registry
     */
    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "calligraphy-locks", 30000);
    }
}
