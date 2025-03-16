package com.example.devin.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for circuit breaker pattern
 * This helps improve system availability by preventing cascading failures
 */
@Configuration
public class CircuitBreakerConfig {

    /**
     * Create a circuit breaker registry with default configuration
     * @return the circuit breaker registry
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        // Configure circuit breaker with appropriate thresholds
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig config = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Percentage of failures to trip the circuit
                .waitDurationInOpenState(Duration.ofMillis(1000)) // Time circuit stays open before switching to half-open
                .permittedNumberOfCallsInHalfOpenState(2) // Number of calls allowed in half-open state
                .slidingWindowSize(10) // Number of calls considered for failure rate
                .recordExceptions(Exception.class) // Record all exceptions as failures
                .build();
        
        return CircuitBreakerRegistry.of(config);
    }
    
    /**
     * Create a circuit breaker for database operations
     * @param circuitBreakerRegistry the circuit breaker registry
     * @return the database circuit breaker
     */
    @Bean
    public CircuitBreaker databaseCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker("database");
    }
    
    /**
     * Create a circuit breaker for external service calls
     * @param circuitBreakerRegistry the circuit breaker registry
     * @return the external service circuit breaker
     */
    @Bean
    public CircuitBreaker externalServiceCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker("externalService");
    }
}
