package com.example.devin.aop;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Aspect for applying bulkhead pattern to controller methods
 * This helps improve system availability by limiting concurrent calls to services
 */
@Aspect
@Component
public class BulkheadAspect {

    private static final Logger logger = Logger.getLogger(BulkheadAspect.class.getName());
    
    @Autowired
    private BulkheadRegistry bulkheadRegistry;
    
    /**
     * Apply bulkhead to product controller methods
     */
    @Around("execution(* com.example.devin.controller.ProductController.*(..))")
    public Object applyProductBulkhead(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String operationName = className + "." + methodName;
        
        Bulkhead bulkhead = bulkheadRegistry.bulkhead("productService");
        
        Supplier<Object> bulkheadSupplier = Bulkhead.decorateSupplier(
                bulkhead, 
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
        
        try {
            return bulkheadSupplier.get();
        } catch (BulkheadFullException e) {
            logger.warning("Bulkhead full for " + operationName);
            
            // Return a service unavailable response
            if (joinPoint.getSignature().getDeclaringType().isAssignableFrom(ResponseEntity.class)) {
                return ResponseEntity.status(503)
                        .body("Service is currently experiencing high load. Please try again later.");
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Apply bulkhead to order controller methods
     */
    @Around("execution(* com.example.devin.controller.OrderController.*(..))")
    public Object applyOrderBulkhead(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String operationName = className + "." + methodName;
        
        Bulkhead bulkhead = bulkheadRegistry.bulkhead("orderService");
        
        Supplier<Object> bulkheadSupplier = Bulkhead.decorateSupplier(
                bulkhead, 
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
        
        try {
            return bulkheadSupplier.get();
        } catch (BulkheadFullException e) {
            logger.warning("Bulkhead full for " + operationName);
            
            // Return a service unavailable response
            if (joinPoint.getSignature().getDeclaringType().isAssignableFrom(ResponseEntity.class)) {
                return ResponseEntity.status(503)
                        .body("Order service is currently experiencing high load. Please try again later.");
            } else {
                throw e;
            }
        }
    }
}
