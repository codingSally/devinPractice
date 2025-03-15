package com.orchestration.process.controller;

import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.service.ProcessDefinitionService;
import com.orchestration.process.service.ProcessExecutionService;
import com.orchestration.process.util.MathProcessBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Controller for demonstrating mathematical process execution.
 */
@RestController
@RequestMapping("/api/math")
public class MathProcessController {
    
    private final ProcessDefinitionService processDefinitionService;
    private final ProcessExecutionService processExecutionService;
    
    @Autowired
    public MathProcessController(
            ProcessDefinitionService processDefinitionService,
            ProcessExecutionService processExecutionService) {
        this.processDefinitionService = processDefinitionService;
        this.processExecutionService = processExecutionService;
    }
    
    /**
     * Execute the example mathematical process
     * @return the result of the process execution
     */
    @GetMapping("/example")
    public ResponseEntity<?> executeExampleProcess() {
        try {
            // Create the example process
            ProcessDefinition processDefinition = MathProcessBuilder.buildExampleProcess();
            
            // Save the process definition
            processDefinition = processDefinitionService.createProcessDefinition(processDefinition);
            
            // Execute the process
            CompletableFuture<ProcessDefinition> future = processExecutionService.executeProcess(processDefinition.getId());
            
            // Wait for the process to complete
            ProcessDefinition result = future.get();
            
            // Return the result
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.badRequest().body("Process execution failed: " + e.getMessage());
        }
    }
}
