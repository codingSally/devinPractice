package com.example.devin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

/**
 * Configuration for rate limiting
 * This helps protect the application from excessive requests in high concurrency scenarios
 */
@Configuration
public class RateLimiterConfig {

    // Cache of rate limiters for each API key or user
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Create a rate limiter for a specific key (API key or user ID)
     * @param key the key to rate limit
     * @return the rate limiter bucket
     */
    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::createNewBucket);
    }

    /**
     * Create a new rate limiter bucket with appropriate limits
     * @param key the key for the bucket
     * @return the new bucket
     */
    private Bucket createNewBucket(String key) {
        // Allow 20 requests per minute
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }

    /**
     * Create a custom rate limiter with specific limits
     * @param capacity the maximum number of tokens
     * @param refillTokens the number of tokens to refill
     * @param refillDuration the duration for token refill
     * @return the new bucket
     */
    public Bucket createLimiter(int capacity, int refillTokens, Duration refillDuration) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(refillTokens, refillDuration));
        return Bucket4j.builder().addLimit(limit).build();
    }
}
