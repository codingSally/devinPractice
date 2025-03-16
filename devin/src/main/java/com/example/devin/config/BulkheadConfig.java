package com.example.devin.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for bulkhead pattern
 * This helps improve system availability by limiting concurrent calls to services
 */
@Configuration
public class BulkheadConfig {

    /**
     * Create a bulkhead registry with default configuration
     * @return the bulkhead registry
     */
    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        // Configure bulkhead with appropriate settings
        io.github.resilience4j.bulkhead.BulkheadConfig config = io.github.resilience4j.bulkhead.BulkheadConfig.custom()
                .maxConcurrentCalls(10) // Maximum number of concurrent calls
                .maxWaitDuration(Duration.ofMillis(500)) // Maximum wait time for a permit
                .build();
        
        return BulkheadRegistry.of(config);
    }
    
    /**
     * Create a bulkhead for product service
     * @param bulkheadRegistry the bulkhead registry
     * @return the product service bulkhead
     */
    @Bean
    public Bulkhead productServiceBulkhead(BulkheadRegistry bulkheadRegistry) {
        return bulkheadRegistry.bulkhead("productService");
    }
    
    /**
     * Create a bulkhead for order service
     * @param bulkheadRegistry the bulkhead registry
     * @return the order service bulkhead
     */
    @Bean
    public Bulkhead orderServiceBulkhead(BulkheadRegistry bulkheadRegistry) {
        return bulkheadRegistry.bulkhead("orderService");
    }
}
