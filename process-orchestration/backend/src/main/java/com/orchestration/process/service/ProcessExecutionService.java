package com.orchestration.process.service;

import com.orchestration.process.core.ProcessOrchestrator;
import com.orchestration.process.model.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for executing process definitions.
 */
@Service
public class ProcessExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(ProcessExecutionService.class);
    
    private final ProcessOrchestrator processOrchestrator;
    private final ProcessDefinitionService processDefinitionService;
    private final Map<Long, CompletableFuture<ProcessDefinition>> runningProcesses = new ConcurrentHashMap<>();
    
    @Autowired
    public ProcessExecutionService(
            ProcessOrchestrator processOrchestrator,
            ProcessDefinitionService processDefinitionService) {
        this.processOrchestrator = processOrchestrator;
        this.processDefinitionService = processDefinitionService;
    }
    
    /**
     * Execute a process definition
     * @param processId the ID of the process definition to execute
     * @return a CompletableFuture that will be completed when the process execution is done
     */
    public CompletableFuture<ProcessDefinition> executeProcess(Long processId) {
        ProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(processId);
        
        if (processDefinition == null) {
            CompletableFuture<ProcessDefinition> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Process definition not found: " + processId));
            return future;
        }
        
        // Check if the process is already running
        if (runningProcesses.containsKey(processId)) {
            logger.warn("Process is already running: {}", processId);
            return runningProcesses.get(processId);
        }
        
        // Execute the process
        CompletableFuture<ProcessDefinition> future = processOrchestrator.execute(processDefinition);
        
        // Store the future for status tracking
        runningProcesses.put(processId, future);
        
        // Remove the future when the process is done
        future.whenComplete((result, error) -> {
            runningProcesses.remove(processId);
            
            if (error != null) {
                logger.error("Process execution failed: {}", processId, error);
            } else {
                logger.info("Process execution completed: {}", processId);
                
                // Update the process definition
                processDefinitionService.updateProcessDefinition(processId, result);
            }
        });
        
        return future;
    }
    
    /**
     * Get the status of a running process
     * @param processId the ID of the process
     * @return the process definition with updated status, or null if not found
     */
    public ProcessDefinition getProcessStatus(Long processId) {
        // Check if the process is running
        if (runningProcesses.containsKey(processId)) {
            return processOrchestrator.getStatus(processId);
        }
        
        // If not running, get the stored process definition
        return processDefinitionService.getProcessDefinition(processId);
    }
    
    /**
     * Stop a running process
     * @param processId the ID of the process to stop
     * @return true if the process was stopped, false otherwise
     */
    public boolean stopProcess(Long processId) {
        // Check if the process is running
        if (runningProcesses.containsKey(processId)) {
            return processOrchestrator.stopProcess(processId);
        }
        
        return false;
    }
    
    /**
     * Check if a process is running
     * @param processId the ID of the process
     * @return true if the process is running, false otherwise
     */
    public boolean isProcessRunning(Long processId) {
        return runningProcesses.containsKey(processId);
    }
}
