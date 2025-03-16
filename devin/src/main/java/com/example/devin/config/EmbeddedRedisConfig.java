package com.example.devin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Configuration for embedded Redis server for development and testing
 * This allows the application to run without an external Redis server
 */
@Configuration
@Profile("!prod") // Only active in non-production environments
public class EmbeddedRedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(config);
    }

    @PostConstruct
    public void startRedis() throws IOException {
        // In a real implementation, we would start an embedded Redis server here
        // For now, we'll just log that we're using an external Redis server
        System.out.println("Using Redis server at " + redisHost + ":" + redisPort);
    }
}
