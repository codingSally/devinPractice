package com.orchestration.process.core;

import com.orchestration.process.model.ProcessNode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating process nodes.
 * This class provides methods to create different types of process nodes.
 */
@Component
public class ProcessNodeFactory {
    private final Map<String, Supplier<ProcessNode>> nodeCreators = new HashMap<>();
    
    public ProcessNodeFactory() {
        // Register default node types
        registerNodeType("logging", () -> {
            ProcessNode node = new ProcessNode();
            node.setType("logging");
            node.setProperties(new ConcurrentHashMap<>());
            return node;
        });
        
        registerNodeType("http", () -> {
            ProcessNode node = new ProcessNode();
            node.setType("http");
            node.setProperties(new ConcurrentHashMap<>());
            return node;
        });
        
        registerNodeType("script", () -> {
            ProcessNode node = new ProcessNode();
            node.setType("script");
            node.setProperties(new ConcurrentHashMap<>());
            return node;
        });
        
        registerNodeType("conditional", () -> {
            ProcessNode node = new ProcessNode();
            node.setType("conditional");
            node.setProperties(new ConcurrentHashMap<>());
            return node;
        });
    }
    
    /**
     * Register a new node type
     * @param type the type of the node
     * @param creator a supplier that creates a new node of the given type
     */
    public void registerNodeType(String type, Supplier<ProcessNode> creator) {
        nodeCreators.put(type, creator);
    }
    
    /**
     * Create a new node of the given type
     * @param type the type of the node to create
     * @return a new node of the given type, or null if the type is not registered
     */
    public ProcessNode createNode(String type) {
        Supplier<ProcessNode> creator = nodeCreators.get(type);
        if (creator == null) {
            return null;
        }
        return creator.get();
    }
    
    /**
     * Check if a node type is registered
     * @param type the type to check
     * @return true if the type is registered, false otherwise
     */
    public boolean hasNodeType(String type) {
        return nodeCreators.containsKey(type);
    }
    
    /**
     * Get all registered node types
     * @return a set of all registered node types
     */
    public Iterable<String> getNodeTypes() {
        return nodeCreators.keySet();
    }
}
