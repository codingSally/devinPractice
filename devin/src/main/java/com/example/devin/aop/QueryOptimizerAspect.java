package com.example.devin.aop;

import com.example.devin.util.QueryOptimizer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Aspect for optimizing SQL queries
 * Intercepts MyBatis mapper methods to optimize queries
 */
@Aspect
@Component
public class QueryOptimizerAspect {

    private static final Logger logger = Logger.getLogger(QueryOptimizerAspect.class.getName());
    
    @Autowired
    private QueryOptimizer queryOptimizer;
    
    /**
     * Intercept and optimize SQL queries in MyBatis mappers
     */
    @Around("execution(* com.example.devin.mapper.*Mapper.*(..))")
    public Object optimizeQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get method name and class
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        // Log the method call
        logger.info("Executing database query: " + className + "." + methodName);
        
        // Start timing
        long startTime = System.currentTimeMillis();
        
        // Execute the method
        Object result = joinPoint.proceed();
        
        // Calculate execution time
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Log slow queries
        if (executionTime > 100) {
            logger.warning("Slow database query: " + className + "." + methodName + 
                          " took " + executionTime + "ms");
        }
        
        return result;
    }
}
