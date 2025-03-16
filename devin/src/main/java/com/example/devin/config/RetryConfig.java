package com.example.devin.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for retry mechanism
 * This helps improve system availability by automatically retrying failed operations
 */
@Configuration
public class RetryConfig {

    /**
     * Create a retry registry with default configuration
     * @return the retry registry
     */
    @Bean
    public RetryRegistry retryRegistry() {
        // Configure retry with appropriate settings
        io.github.resilience4j.retry.RetryConfig config = io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(3) // Maximum number of retry attempts
                .waitDuration(Duration.ofMillis(1000)) // Wait time between retries
                .retryExceptions(Exception.class) // Retry on all exceptions
                .build();
        
        return RetryRegistry.of(config);
    }
    
    /**
     * Create a retry for database operations
     * @param retryRegistry the retry registry
     * @return the database retry
     */
    @Bean
    public Retry databaseRetry(RetryRegistry retryRegistry) {
        return retryRegistry.retry("database");
    }
    
    /**
     * Create a retry for external service calls
     * @param retryRegistry the retry registry
     * @return the external service retry
     */
    @Bean
    public Retry externalServiceRetry(RetryRegistry retryRegistry) {
        return retryRegistry.retry("externalService");
    }
}
