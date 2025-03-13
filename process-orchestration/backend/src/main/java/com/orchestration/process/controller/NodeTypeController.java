package com.orchestration.process.controller;

import com.orchestration.process.core.ProcessNodeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for retrieving available node types.
 */
@RestController
@RequestMapping("/node-types")
public class NodeTypeController {
    private final ProcessNodeFactory processNodeFactory;
    
    @Autowired
    public NodeTypeController(ProcessNodeFactory processNodeFactory) {
        this.processNodeFactory = processNodeFactory;
    }
    
    /**
     * Get all available node types
     * @return a list of all available node types
     */
    @GetMapping
    public ResponseEntity<List<String>> getNodeTypes() {
        List<String> nodeTypes = new ArrayList<>();
        processNodeFactory.getNodeTypes().forEach(nodeTypes::add);
        return ResponseEntity.ok(nodeTypes);
    }
}
