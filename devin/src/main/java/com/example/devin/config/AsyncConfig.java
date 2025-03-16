package com.example.devin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for asynchronous task execution
 * This enables high concurrency by allowing tasks to be processed asynchronously
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Task executor for handling asynchronous operations
     * Configured with optimal thread pool settings for high concurrency
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Set core pool size - number of threads to keep alive even when idle
        executor.setCorePoolSize(5);
        // Maximum pool size - maximum number of threads to allow in the pool
        executor.setMaxPoolSize(10);
        // Queue capacity - how many tasks can wait in queue when all threads are busy
        executor.setQueueCapacity(100);
        // Thread name prefix - helps with debugging
        executor.setThreadNamePrefix("CalligraphyAsync-");
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // Initialize the executor
        executor.initialize();
        return executor;
    }
}
