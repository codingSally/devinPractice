package com.example.devin.controller;

import com.example.devin.interceptor.PerformanceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for performance monitoring
 * Provides endpoints to view and manage performance metrics
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    @Autowired
    private PerformanceInterceptor performanceInterceptor;

    /**
     * Get performance metrics for all endpoints
     * @return map of endpoint metrics
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Map<String, Object>>> getPerformanceMetrics() {
        return ResponseEntity.ok(performanceInterceptor.getPerformanceMetrics());
    }

    /**
     * Reset performance metrics
     * @return success message
     */
    @DeleteMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetMetrics() {
        performanceInterceptor.resetMetrics();
        return ResponseEntity.ok(Map.of("message", "Performance metrics reset successfully"));
    }
}
