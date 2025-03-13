package com.orchestration.process.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Registry for process node executors.
 * This class maintains a list of all available node executors and provides
 * methods to find the appropriate executor for a given node type.
 */
@Component
public class ProcessNodeExecutorRegistry {
    private final List<ProcessNodeExecutor> executors;
    
    @Autowired
    public ProcessNodeExecutorRegistry(List<ProcessNodeExecutor> executors) {
        this.executors = new ArrayList<>(executors);
    }
    
    @PostConstruct
    public void init() {
        // Log the registered executors
        executors.forEach(executor -> 
            System.out.println("Registered executor: " + executor.getClass().getSimpleName())
        );
    }
    
    /**
     * Find an executor that can handle the given node type
     * @param nodeType the type of the node
     * @return an Optional containing the executor if found, empty otherwise
     */
    public Optional<ProcessNodeExecutor> findExecutor(String nodeType) {
        return executors.stream()
                .filter(executor -> executor.canExecute(nodeType))
                .findFirst();
    }
    
    /**
     * Register a new executor
     * @param executor the executor to register
     */
    public void registerExecutor(ProcessNodeExecutor executor) {
        executors.add(executor);
    }
}
