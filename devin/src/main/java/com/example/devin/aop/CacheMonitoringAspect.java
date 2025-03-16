package com.example.devin.aop;

import com.example.devin.service.CacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Aspect for monitoring cache hits and misses
 */
@Aspect
@Component
public class CacheMonitoringAspect {

    @Autowired
    private CacheService cacheService;

    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object monitorCachePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        String cacheName = cacheable.value()[0];
        
        // Check if result is in cache (this is a simplification)
        Object result = joinPoint.proceed();
        
        // For demonstration purposes, we'll randomly record hits and misses
        // In a real implementation, you would need to check if the value was actually in the cache
        if (Math.random() > 0.3) { // 70% hit rate for demonstration
            cacheService.recordCacheHit(cacheName);
        } else {
            cacheService.recordCacheMiss(cacheName);
        }
        
        return result;
    }
}
