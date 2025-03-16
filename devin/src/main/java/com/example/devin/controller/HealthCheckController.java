package com.example.devin.controller;

import com.example.devin.service.CacheService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for health check endpoints
 * Provides endpoints for monitoring system health and availability
 */
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    /**
     * Basic health check endpoint
     * @return health status
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        return ResponseEntity.ok(status);
    }
    
    /**
     * Detailed health check endpoint
     * @return detailed health status of all components
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        Map<String, Object> components = new HashMap<>();
        
        // Check database
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            components.put("database", Map.of("status", "UP"));
        } catch (Exception e) {
            components.put("database", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
        }
        
        // Check cache
        try {
            cacheService.getCacheNames();
            components.put("cache", Map.of("status", "UP"));
        } catch (Exception e) {
            components.put("cache", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
        }
        
        // Check circuit breakers
        Map<String, Object> circuitBreakers = new HashMap<>();
        for (CircuitBreaker circuitBreaker : circuitBreakerRegistry.getAllCircuitBreakers()) {
            String name = circuitBreaker.getName();
            CircuitBreaker.State state = circuitBreaker.getState();
            CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
            
            Map<String, Object> circuitBreakerInfo = new HashMap<>();
            circuitBreakerInfo.put("state", state.toString());
            circuitBreakerInfo.put("failureRate", metrics.getFailureRate() + "%");
            circuitBreakerInfo.put("numberOfFailedCalls", metrics.getNumberOfFailedCalls());
            circuitBreakerInfo.put("numberOfSuccessfulCalls", metrics.getNumberOfSuccessfulCalls());
            
            circuitBreakers.put(name, circuitBreakerInfo);
        }
        components.put("circuitBreakers", circuitBreakers);
        
        // Overall status
        boolean isDown = components.values().stream()
                .filter(c -> c instanceof Map)
                .map(c -> (Map<String, Object>) c)
                .anyMatch(c -> "DOWN".equals(c.get("status")));
        
        health.put("status", isDown ? "DOWN" : "UP");
        health.put("components", components);
        
        return ResponseEntity.ok(health);
    }
}
