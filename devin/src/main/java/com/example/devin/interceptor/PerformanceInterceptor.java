package com.example.devin.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Interceptor for monitoring API performance
 * Tracks request execution time for performance analysis
 */
public class PerformanceInterceptor implements HandlerInterceptor {

    private static final Logger logger = Logger.getLogger(PerformanceInterceptor.class.getName());
    
    // Performance metrics storage
    private final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalResponseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> maxResponseTimes = new ConcurrentHashMap<>();
    
    // Threshold for slow request logging (in milliseconds)
    private static final long SLOW_REQUEST_THRESHOLD_MS = 200;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Store start time in request attribute
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Not used
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Skip if not a handler method (e.g., static resources)
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        
        // Calculate execution time
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime == null) {
            return;
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Get endpoint name
        String endpoint = getEndpointName(request, handler);
        
        // Update metrics
        requestCounts.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
        totalResponseTimes.computeIfAbsent(endpoint, k -> new AtomicLong(0)).addAndGet(executionTime);
        
        // Update max response time if needed
        maxResponseTimes.computeIfAbsent(endpoint, k -> new AtomicLong(0)).updateAndGet(
                current -> Math.max(current, executionTime));
        
        // Log slow requests
        if (executionTime > SLOW_REQUEST_THRESHOLD_MS) {
            logger.warning("Slow request: " + endpoint + " took " + executionTime + "ms");
        }
    }
    
    /**
     * Get endpoint name from request and handler
     */
    private String getEndpointName(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return request.getMethod() + " " + handlerMethod.getBeanType().getSimpleName() + "." + 
                   handlerMethod.getMethod().getName();
        }
        return request.getMethod() + " " + request.getRequestURI();
    }
    
    /**
     * Get performance metrics
     * @return map of endpoint metrics
     */
    public Map<String, Map<String, Object>> getPerformanceMetrics() {
        Map<String, Map<String, Object>> metrics = new ConcurrentHashMap<>();
        
        for (String endpoint : requestCounts.keySet()) {
            Map<String, Object> endpointMetrics = new ConcurrentHashMap<>();
            
            long count = requestCounts.getOrDefault(endpoint, new AtomicLong(0)).get();
            long totalTime = totalResponseTimes.getOrDefault(endpoint, new AtomicLong(0)).get();
            long maxTime = maxResponseTimes.getOrDefault(endpoint, new AtomicLong(0)).get();
            
            endpointMetrics.put("count", count);
            endpointMetrics.put("totalTimeMs", totalTime);
            endpointMetrics.put("maxTimeMs", maxTime);
            endpointMetrics.put("avgTimeMs", count > 0 ? totalTime / count : 0);
            
            metrics.put(endpoint, endpointMetrics);
        }
        
        return metrics;
    }
    
    /**
     * Reset performance metrics
     */
    public void resetMetrics() {
        requestCounts.clear();
        totalResponseTimes.clear();
        maxResponseTimes.clear();
    }
}
