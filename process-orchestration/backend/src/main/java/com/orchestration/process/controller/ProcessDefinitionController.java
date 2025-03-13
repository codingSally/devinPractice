package com.orchestration.process.controller;

import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.service.ProcessDefinitionService;
import com.orchestration.process.util.ProcessExecutionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing process definitions.
 */
@RestController
@RequestMapping("/process-definitions")
public class ProcessDefinitionController {
    private final ProcessDefinitionService processDefinitionService;
    
    @Autowired
    public ProcessDefinitionController(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }
    
    /**
     * Create a new process definition
     * @param processDefinition the process definition to create
     * @return the created process definition
     */
    @PostMapping
    public ResponseEntity<?> createProcessDefinition(@RequestBody ProcessDefinition processDefinition) {
        // Validate the process definition
        List<String> validationErrors = ProcessExecutionUtils.validateProcessDefinition(processDefinition);
        
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 
                            "Process definition validation failed: " + validationErrors.get(0))
            );
        }
        
        // Calculate node levels
        ProcessExecutionUtils.calculateNodeLevels(processDefinition);
        
        // Create the process definition
        ProcessDefinition createdDefinition = processDefinitionService.createProcessDefinition(processDefinition);
        return ResponseEntity.ok(createdDefinition);
    }
    
    /**
     * Get a process definition by ID
     * @param id the ID of the process definition
     * @return the process definition
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProcessDefinition(@PathVariable Long id) {
        ProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(id);
        
        if (processDefinition == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(processDefinition);
    }
    
    /**
     * Update a process definition
     * @param id the ID of the process definition
     * @param processDefinition the updated process definition
     * @return the updated process definition
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProcessDefinition(
            @PathVariable Long id,
            @RequestBody ProcessDefinition processDefinition) {
        // Validate the process definition
        List<String> validationErrors = ProcessExecutionUtils.validateProcessDefinition(processDefinition);
        
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 
                            "Process definition validation failed: " + validationErrors.get(0))
            );
        }
        
        // Calculate node levels
        ProcessExecutionUtils.calculateNodeLevels(processDefinition);
        
        // Update the process definition
        ProcessDefinition updatedDefinition = processDefinitionService.updateProcessDefinition(id, processDefinition);
        
        if (updatedDefinition == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedDefinition);
    }
    
    /**
     * Delete a process definition
     * @param id the ID of the process definition
     * @return no content if successful, not found otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProcessDefinition(@PathVariable Long id) {
        boolean deleted = processDefinitionService.deleteProcessDefinition(id);
        
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all process definitions
     * @return a list of all process definitions
     */
    @GetMapping
    public ResponseEntity<List<ProcessDefinition>> getAllProcessDefinitions() {
        List<ProcessDefinition> processDefinitions = processDefinitionService.getAllProcessDefinitions();
        return ResponseEntity.ok(processDefinitions);
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
