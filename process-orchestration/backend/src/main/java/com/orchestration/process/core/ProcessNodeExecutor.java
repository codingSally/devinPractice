package com.orchestration.process.core;

import com.orchestration.process.model.ProcessNode;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for executing process nodes.
 * Implementations should handle the execution logic for specific node types.
 */
public interface ProcessNodeExecutor {
    /**
     * Check if this executor can handle the given node type
     * @param nodeType the type of the node
     * @return true if this executor can handle the node type, false otherwise
     */
    boolean canExecute(String nodeType);
    
    /**
     * Execute the node asynchronously
     * @param node the node to execute
     * @return a CompletableFuture that will be completed when the node execution is done
     */
    CompletableFuture<Object> execute(ProcessNode node);
}
