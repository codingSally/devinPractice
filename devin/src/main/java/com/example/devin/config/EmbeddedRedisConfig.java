package com.example.devin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

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

    private RedisServer redisServer;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(config);
    }

    @PostConstruct
    public void startRedis() throws IOException {
        // Skip if external Redis is configured
        if (!StringUtils.hasText(redisHost) || !redisHost.equals("localhost")) {
            return;
        }

        try {
            redisServer = RedisServer.builder()
                    .port(redisPort)
                    .setting("maxmemory 128M") // Limit memory usage
                    .build();
            
            redisServer.start();
        } catch (Exception e) {
            // Redis might already be running, which is fine
            System.out.println("Could not start embedded Redis: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }
}
