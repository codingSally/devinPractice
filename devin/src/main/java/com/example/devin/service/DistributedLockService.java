package com.example.devin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Service for distributed locking
 * This helps improve system availability in distributed environments
 */
@Service
public class DistributedLockService {

    private static final Logger logger = Logger.getLogger(DistributedLockService.class.getName());
    
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    
    /**
     * Execute a function with a distributed lock
     * @param lockKey the lock key
     * @param timeout the timeout in seconds
     * @param supplier the function to execute
     * @return the result of the function
     */
    public <T> T executeWithLock(String lockKey, long timeout, Supplier<T> supplier) {
        Lock lock = redisLockRegistry.obtain(lockKey);
        
        try {
            boolean acquired = lock.tryLock(timeout, TimeUnit.SECONDS);
            if (acquired) {
                try {
                    logger.info("Acquired lock: " + lockKey);
                    return supplier.get();
                } finally {
                    lock.unlock();
                    logger.info("Released lock: " + lockKey);
                }
            } else {
                logger.warning("Failed to acquire lock: " + lockKey);
                throw new RuntimeException("Failed to acquire lock: " + lockKey);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Lock acquisition interrupted: " + e.getMessage());
            throw new RuntimeException("Lock acquisition interrupted", e);
        }
    }
    
    /**
     * Execute a function with a distributed lock
     * @param lockKey the lock key
     * @param supplier the function to execute
     * @return the result of the function
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, 10, supplier); // Default timeout of 10 seconds
    }
    
    /**
     * Execute a runnable with a distributed lock
     * @param lockKey the lock key
     * @param timeout the timeout in seconds
     * @param runnable the runnable to execute
     */
    public void executeWithLock(String lockKey, long timeout, Runnable runnable) {
        executeWithLock(lockKey, timeout, () -> {
            runnable.run();
            return null;
        });
    }
    
    /**
     * Execute a runnable with a distributed lock
     * @param lockKey the lock key
     * @param runnable the runnable to execute
     */
    public void executeWithLock(String lockKey, Runnable runnable) {
        executeWithLock(lockKey, 10, runnable); // Default timeout of 10 seconds
    }
}
