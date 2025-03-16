package com.example.devin.aop;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Aspect for applying retry pattern to service methods
 * This helps improve system availability by automatically retrying failed operations
 */
@Aspect
@Component
public class RetryAspect {

    private static final Logger logger = Logger.getLogger(RetryAspect.class.getName());
    
    @Autowired
    private RetryRegistry retryRegistry;
    
    /**
     * Apply retry to database operations
     */
    @Around("execution(* com.example.devin.mapper.*Mapper.*(..))")
    public Object applyDatabaseRetry(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String operationName = className + "." + methodName;
        
        Retry retry = retryRegistry.retry("database");
        
        Supplier<Object> retryableSupplier = Retry.decorateSupplier(
                retry, 
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
        
        try {
            return retryableSupplier.get();
        } catch (Exception e) {
            logger.severe("All retry attempts failed for database operation " + 
                         operationName + ": " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Apply retry to external service calls
     */
    @Around("execution(* com.example.devin.service.external.*.*(..))")
    public Object applyExternalServiceRetry(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String operationName = className + "." + methodName;
        
        Retry retry = retryRegistry.retry("externalService");
        
        Supplier<Object> retryableSupplier = Retry.decorateSupplier(
                retry, 
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
        
        try {
            return retryableSupplier.get();
        } catch (Exception e) {
            logger.severe("All retry attempts failed for external service " + 
                         operationName + ": " + e.getMessage());
            throw e;
        }
    }
}
