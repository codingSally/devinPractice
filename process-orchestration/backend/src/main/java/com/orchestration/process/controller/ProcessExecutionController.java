package com.orchestration.process.controller;

import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.service.ProcessExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Controller for executing processes.
 */
@RestController
@RequestMapping("/process-executions")
public class ProcessExecutionController {
    private final ProcessExecutionService processExecutionService;
    
    @Autowired
    public ProcessExecutionController(ProcessExecutionService processExecutionService) {
        this.processExecutionService = processExecutionService;
    }
    
    /**
     * Execute a process
     * @param processId the ID of the process to execute
     * @param waitForCompletion whether to wait for the process to complete
     * @param timeoutSeconds timeout in seconds if waiting for completion
     * @return the process execution status
     */
    @PostMapping("/{processId}")
    public ResponseEntity<?> executeProcess(
            @PathVariable Long processId,
            @RequestParam(required = false, defaultValue = "false") boolean waitForCompletion,
            @RequestParam(required = false, defaultValue = "30") int timeoutSeconds) {
        
        CompletableFuture<ProcessDefinition> future = processExecutionService.executeProcess(processId);
        
        // If not waiting for completion, return immediately
        if (!waitForCompletion) {
            Map<String, Object> response = new HashMap<>();
            response.put("processId", processId);
            response.put("message", "Process execution started");
            response.put("status", "RUNNING");
            
            return ResponseEntity.ok(response);
        }
        
        // Wait for completion
        try {
            ProcessDefinition result = future.get(timeoutSeconds, TimeUnit.SECONDS);
            return ResponseEntity.ok(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                            "Process execution interrupted"));
        } catch (ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                            "Process execution failed: " + e.getCause().getMessage()));
        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body(new ErrorResponse(HttpStatus.REQUEST_TIMEOUT.value(), 
                            "Process execution timed out after " + timeoutSeconds + " seconds"));
        }
    }
    
    /**
     * Get the status of a process
     * @param processId the ID of the process
     * @return the process status
     */
    @GetMapping("/{processId}")
    public ResponseEntity<?> getProcessStatus(@PathVariable Long processId) {
        ProcessDefinition processDefinition = processExecutionService.getProcessStatus(processId);
        
        if (processDefinition == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(processDefinition);
    }
    
    /**
     * Stop a running process
     * @param processId the ID of the process to stop
     * @return success or failure message
     */
    @DeleteMapping("/{processId}")
    public ResponseEntity<?> stopProcess(@PathVariable Long processId) {
        boolean stopped = processExecutionService.stopProcess(processId);
        
        if (!stopped) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 
                            "Process not found or not running: " + processId));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("processId", processId);
        response.put("message", "Process execution stopped");
        response.put("status", "STOPPED");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Error response class
     */
    private static class ErrorResponse {
        private final int status;
        private final String message;
        
        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }
        
        public int getStatus() {
            return status;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
