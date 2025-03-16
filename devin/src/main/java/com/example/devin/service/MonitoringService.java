package com.example.devin.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Service for system monitoring and alerting
 * Monitors system health and sends alerts for abnormal conditions
 */
@Service
public class MonitoringService {

    private static final Logger logger = Logger.getLogger(MonitoringService.class.getName());
    
    // Thresholds for alerting
    private static final int ERROR_THRESHOLD = 5; // Number of errors to trigger alert
    private static final int SLOW_REQUEST_THRESHOLD = 10; // Number of slow requests to trigger alert
    
    // Counters for monitoring
    private final Map<String, AtomicInteger> errorCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> slowRequestCounts = new ConcurrentHashMap<>();
    
    // Alert status to prevent repeated alerts
    private final Map<String, Boolean> alertSent = new ConcurrentHashMap<>();
    
    /**
     * Record an error for monitoring
     * @param component the component where the error occurred
     * @param errorMessage the error message
     */
    public void recordError(String component, String errorMessage) {
        logger.warning("Error in " + component + ": " + errorMessage);
        
        // Increment error count
        errorCounts.computeIfAbsent(component, k -> new AtomicInteger(0))
                  .incrementAndGet();
        
        // Check if alert threshold is reached
        if (errorCounts.get(component).get() >= ERROR_THRESHOLD) {
            sendAlert(component, "High error rate", 
                     "Component " + component + " has reached error threshold");
        }
    }
    
    /**
     * Record a slow request for monitoring
     * @param endpoint the endpoint with slow response
     * @param durationMs the request duration in milliseconds
     */
    public void recordSlowRequest(String endpoint, long durationMs) {
        logger.warning("Slow request to " + endpoint + ": " + durationMs + "ms");
        
        // Increment slow request count
        slowRequestCounts.computeIfAbsent(endpoint, k -> new AtomicInteger(0))
                        .incrementAndGet();
        
        // Check if alert threshold is reached
        if (slowRequestCounts.get(endpoint).get() >= SLOW_REQUEST_THRESHOLD) {
            sendAlert(endpoint, "Slow response time", 
                     "Endpoint " + endpoint + " has slow response time: " + durationMs + "ms");
        }
    }
    
    /**
     * Send an alert for abnormal conditions
     * @param component the component with the issue
     * @param alertType the type of alert
     * @param message the alert message
     */
    private void sendAlert(String component, String alertType, String message) {
        String alertKey = component + ":" + alertType;
        
        // Check if alert was already sent
        if (alertSent.getOrDefault(alertKey, false)) {
            return;
        }
        
        // In a real application, this would send an email, SMS, or notification
        logger.severe("ALERT: " + alertType + " - " + message);
        
        // Mark alert as sent
        alertSent.put(alertKey, true);
    }
    
    /**
     * Reset alert status periodically
     * This allows new alerts to be sent after a cool-down period
     */
    @Scheduled(fixedRate = 3600000) // Reset every hour
    public void resetAlertStatus() {
        alertSent.clear();
        logger.info("Alert status reset");
    }
    
    /**
     * Reset counters periodically
     * This prevents counters from growing indefinitely
     */
    @Scheduled(fixedRate = 86400000) // Reset every day
    public void resetCounters() {
        errorCounts.clear();
        slowRequestCounts.clear();
        logger.info("Monitoring counters reset");
    }
    
    /**
     * Get current monitoring metrics
     * @return map of monitoring metrics
     */
    public Map<String, Object> getMonitoringMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Add error counts
        Map<String, Integer> errors = new HashMap<>();
        errorCounts.forEach((component, count) -> errors.put(component, count.get()));
        metrics.put("errors", errors);
        
        // Add slow request counts
        Map<String, Integer> slowRequests = new HashMap<>();
        slowRequestCounts.forEach((endpoint, count) -> slowRequests.put(endpoint, count.get()));
        metrics.put("slowRequests", slowRequests);
        
        return metrics;
    }
}
