package com.orchestration.process.core;

import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.model.ProcessNode;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for orchestrating process execution.
 * Implementations should handle the traversal and execution of process nodes.
 */
public interface ProcessOrchestrator {
    /**
     * Execute a process definition
     * @param processDefinition the process definition to execute
     * @return a CompletableFuture that will be completed when the process execution is done
     */
    CompletableFuture<ProcessDefinition> execute(ProcessDefinition processDefinition);
    
    /**
     * Get the current execution status
     * @param processId the ID of the process
     * @return the process definition with updated status
     */
    ProcessDefinition getStatus(Long processId);
    
    /**
     * Stop a running process
     * @param processId the ID of the process to stop
     * @return true if the process was stopped, false otherwise
     */
    boolean stopProcess(Long processId);
}
