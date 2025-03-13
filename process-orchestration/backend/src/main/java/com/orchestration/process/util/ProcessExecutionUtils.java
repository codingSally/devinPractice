package com.orchestration.process.util;

import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.model.ProcessNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for process execution.
 */
public class ProcessExecutionUtils {
    
    /**
     * Calculate the level of each node in the process definition
     * @param processDefinition the process definition
     */
    public static void calculateNodeLevels(ProcessDefinition processDefinition) {
        // Reset all levels
        processDefinition.getNodes().forEach(node -> node.setLevel(null));
        
        // Get the root nodes
        List<ProcessNode> rootNodes = processDefinition.getRootNodeIds().stream()
                .map(processDefinition::getNodeById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // Set the level of root nodes to 0
        rootNodes.forEach(node -> node.setLevel(0));
        
        // Initialize the queue with root nodes
        Queue<ProcessNode> queue = new LinkedList<>(rootNodes);
        Set<String> visited = new HashSet<>();
        
        // Process each node
        while (!queue.isEmpty()) {
            ProcessNode node = queue.poll();
            
            if (visited.contains(node.getNodeId())) {
                continue;
            }
            
            visited.add(node.getNodeId());
            
            // Process child nodes
            for (String childId : node.getChildNodeIds()) {
                ProcessNode childNode = processDefinition.getNodeById(childId);
                
                if (childNode != null) {
                    // Set the level of the child node
                    if (childNode.getLevel() == null || childNode.getLevel() > node.getLevel() + 1) {
                        childNode.setLevel(node.getLevel() + 1);
                    }
                    
                    queue.add(childNode);
                }
            }
        }
    }
    
    /**
     * Validate a process definition
     * @param processDefinition the process definition to validate
     * @return a list of validation errors, empty if the process definition is valid
     */
    public static List<String> validateProcessDefinition(ProcessDefinition processDefinition) {
        List<String> errors = new ArrayList<>();
        
        // Check if the process definition has a name
        if (processDefinition.getName() == null || processDefinition.getName().isEmpty()) {
            errors.add("Process definition must have a name");
        }
        
        // Check if the process definition has at least one node
        if (processDefinition.getNodes() == null || processDefinition.getNodes().isEmpty()) {
            errors.add("Process definition must have at least one node");
        }
        
        // Check if the process definition has at least one root node
        if (processDefinition.getRootNodeIds() == null || processDefinition.getRootNodeIds().isEmpty()) {
            errors.add("Process definition must have at least one root node");
        }
        
        // Build the node map if not already built
        if (processDefinition.getNodeMap() == null || processDefinition.getNodeMap().isEmpty()) {
            processDefinition.buildNodeMap();
        }
        
        // Check if all root nodes exist
        for (String rootNodeId : processDefinition.getRootNodeIds()) {
            if (processDefinition.getNodeById(rootNodeId) == null) {
                errors.add("Root node not found: " + rootNodeId);
            }
        }
        
        // Check if all child nodes exist
        for (ProcessNode node : processDefinition.getNodes()) {
            for (String childId : node.getChildNodeIds()) {
                if (processDefinition.getNodeById(childId) == null) {
                    errors.add("Child node not found: " + childId + " (referenced by node " + node.getNodeId() + ")");
                }
            }
        }
        
        // Check for cycles
        if (hasCycle(processDefinition)) {
            errors.add("Process definition contains a cycle");
        }
        
        return errors;
    }
    
    /**
     * Check if a process definition contains a cycle
     * @param processDefinition the process definition to check
     * @return true if the process definition contains a cycle, false otherwise
     */
    private static boolean hasCycle(ProcessDefinition processDefinition) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        // Check each root node
        for (String rootNodeId : processDefinition.getRootNodeIds()) {
            if (hasCycleUtil(processDefinition, rootNodeId, visited, recursionStack)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Utility method for checking cycles
     * @param processDefinition the process definition
     * @param nodeId the current node ID
     * @param visited set of visited nodes
     * @param recursionStack set of nodes in the current recursion stack
     * @return true if a cycle is found, false otherwise
     */
    private static boolean hasCycleUtil(
            ProcessDefinition processDefinition,
            String nodeId,
            Set<String> visited,
            Set<String> recursionStack) {
        // Mark the current node as visited and add to recursion stack
        visited.add(nodeId);
        recursionStack.add(nodeId);
        
        // Get the node
        ProcessNode node = processDefinition.getNodeById(nodeId);
        
        if (node != null) {
            // Check all child nodes
            for (String childId : node.getChildNodeIds()) {
                // If the child node is not visited, check it recursively
                if (!visited.contains(childId)) {
                    if (hasCycleUtil(processDefinition, childId, visited, recursionStack)) {
                        return true;
                    }
                } else if (recursionStack.contains(childId)) {
                    // If the child node is in the recursion stack, there is a cycle
                    return true;
                }
            }
        }
        
        // Remove the current node from the recursion stack
        recursionStack.remove(nodeId);
        return false;
    }
}
