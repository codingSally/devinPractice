package com.example.devin.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing application caches
 */
@Service
public class CacheService {

    private final CacheManager cacheManager;
    
    // Cache statistics
    private final Map<String, AtomicLong> cacheHits = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> cacheMisses = new ConcurrentHashMap<>();

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    /**
     * Clear a specific cache
     * @param cacheName name of the cache to clear
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Get all cache names
     * @return collection of cache names
     */
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }
    
    /**
     * Record a cache hit
     * @param cacheName name of the cache
     */
    public void recordCacheHit(String cacheName) {
        cacheHits.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Record a cache miss
     * @param cacheName name of the cache
     */
    public void recordCacheMiss(String cacheName) {
        cacheMisses.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Get cache statistics
     * @return map of cache statistics
     */
    public Map<String, Map<String, Long>> getCacheStatistics() {
        Map<String, Map<String, Long>> statistics = new HashMap<>();
        
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(cacheName -> {
            Map<String, Long> cacheStats = new HashMap<>();
            cacheStats.put("hits", cacheHits.getOrDefault(cacheName, new AtomicLong(0)).get());
            cacheStats.put("misses", cacheMisses.getOrDefault(cacheName, new AtomicLong(0)).get());
            statistics.put(cacheName, cacheStats);
        });
        
        return statistics;
    }
}
