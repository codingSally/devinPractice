package com.orchestration.process.service;

import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.model.ProcessNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing process definitions.
 */
@Service
public class ProcessDefinitionService {
    private final Map<Long, ProcessDefinition> processDefinitions = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * Create a new process definition
     * @param processDefinition the process definition to create
     * @return the created process definition with generated ID
     */
    public ProcessDefinition createProcessDefinition(ProcessDefinition processDefinition) {
        Long id = idGenerator.getAndIncrement();
        processDefinition.setId(id);
        
        // Assign IDs to nodes if not already assigned
        Map<String, ProcessNode> nodeMap = new HashMap<>();
        for (ProcessNode node : processDefinition.getNodes()) {
            if (node.getNodeId() == null || node.getNodeId().isEmpty()) {
                node.setNodeId("node-" + idGenerator.getAndIncrement());
            }
            nodeMap.put(node.getNodeId(), node);
        }
        
        // Build the node map
        processDefinition.setNodeMap(nodeMap);
        
        processDefinitions.put(id, processDefinition);
        return processDefinition;
    }
    
    /**
     * Get a process definition by ID
     * @param id the ID of the process definition
     * @return the process definition, or null if not found
     */
    public ProcessDefinition getProcessDefinition(Long id) {
        return processDefinitions.get(id);
    }
    
    /**
     * Update a process definition
     * @param id the ID of the process definition to update
     * @param processDefinition the updated process definition
     * @return the updated process definition, or null if not found
     */
    public ProcessDefinition updateProcessDefinition(Long id, ProcessDefinition processDefinition) {
        if (!processDefinitions.containsKey(id)) {
            return null;
        }
        
        processDefinition.setId(id);
        
        // Rebuild the node map
        Map<String, ProcessNode> nodeMap = new HashMap<>();
        for (ProcessNode node : processDefinition.getNodes()) {
            nodeMap.put(node.getNodeId(), node);
        }
        processDefinition.setNodeMap(nodeMap);
        
        processDefinitions.put(id, processDefinition);
        return processDefinition;
    }
    
    /**
     * Delete a process definition
     * @param id the ID of the process definition to delete
     * @return true if the process definition was deleted, false otherwise
     */
    public boolean deleteProcessDefinition(Long id) {
        return processDefinitions.remove(id) != null;
    }
    
    /**
     * Get all process definitions
     * @return a list of all process definitions
     */
    public List<ProcessDefinition> getAllProcessDefinitions() {
        return new ArrayList<>(processDefinitions.values());
    }
}
