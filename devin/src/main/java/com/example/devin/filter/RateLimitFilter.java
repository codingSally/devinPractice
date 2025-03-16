package com.example.devin.filter;

import com.example.devin.config.RateLimiterConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for rate limiting requests
 * This helps protect the application from excessive requests in high concurrency scenarios
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiterConfig rateLimiterConfig;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Skip rate limiting for certain paths
        String path = request.getRequestURI();
        if (shouldSkipRateLimiting(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get the client identifier (IP address or API key)
        String clientId = getClientIdentifier(request);
        
        // Get the rate limiter for this client
        Bucket bucket = rateLimiterConfig.resolveBucket(clientId);
        
        // Try to consume a token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            // Request allowed, add rate limit headers
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Request denied due to rate limiting
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", 
                    String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }
    
    /**
     * Determine if rate limiting should be skipped for this path
     * @param path the request path
     * @return true if rate limiting should be skipped
     */
    private boolean shouldSkipRateLimiting(String path) {
        // Skip rate limiting for static resources and health checks
        return path.startsWith("/public/") || 
               path.startsWith("/images/") || 
               path.equals("/health") ||
               path.equals("/h2-console");
    }
    
    /**
     * Get a unique identifier for the client
     * @param request the HTTP request
     * @return the client identifier
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // Use API key if available
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }
        
        // Fall back to IP address
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        
        return clientIp;
    }
}
