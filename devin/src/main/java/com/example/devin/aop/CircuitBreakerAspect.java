package com.example.devin.aop;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Aspect for applying circuit breaker pattern to service methods
 * This helps improve system availability by preventing cascading failures
 */
@Aspect
@Component
public class CircuitBreakerAspect {

    private static final Logger logger = Logger.getLogger(CircuitBreakerAspect.class.getName());
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    /**
     * Apply circuit breaker to database operations
     */
    @Around("execution(* com.example.devin.mapper.*Mapper.*(..))")
    public Object applyDatabaseCircuitBreaker(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String operationName = className + "." + methodName;
        
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        
        Supplier<Object> decoratedSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker, 
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
        
        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            logger.severe("Circuit breaker caught exception in database operation " + 
                         operationName + ": " + e.getMessage());
            
            // Provide fallback behavior
            return handleDatabaseFailure(joinPoint, e);
        }
    }
    
    /**
     * Apply circuit breaker to external service calls
     */
    @Around("execution(* com.example.devin.service.external.*.*(..))")
    public Object applyExternalServiceCircuitBreaker(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String operationName = className + "." + methodName;
        
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalService");
        
        Supplier<Object> decoratedSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker, 
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
        
        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            logger.severe("Circuit breaker caught exception in external service " + 
                         operationName + ": " + e.getMessage());
            
            // Provide fallback behavior
            return handleExternalServiceFailure(joinPoint, e);
        }
    }
    
    /**
     * Handle database operation failure with fallback behavior
     */
    private Object handleDatabaseFailure(ProceedingJoinPoint joinPoint, Exception e) {
        String methodName = joinPoint.getSignature().getName();
        
        // Implement fallback behavior based on method name
        if (methodName.startsWith("find") || methodName.startsWith("get")) {
            // For read operations, return empty results
            if (methodName.endsWith("ById")) {
                return null; // Return null for single entity lookups
            } else {
                return java.util.Collections.emptyList(); // Return empty list for collection lookups
            }
        } else if (methodName.startsWith("count")) {
            return 0; // Return zero count
        } else {
            // For write operations, throw exception
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handle external service failure with fallback behavior
     */
    private Object handleExternalServiceFailure(ProceedingJoinPoint joinPoint, Exception e) {
        // Implement fallback behavior for external services
        // This is a simplified implementation
        return null; // Return null as fallback
    }
}
