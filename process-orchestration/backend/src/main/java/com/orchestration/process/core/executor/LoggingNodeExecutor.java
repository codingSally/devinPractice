package com.orchestration.process.core.executor;

import com.orchestration.process.core.ProcessNodeExecutor;
import com.orchestration.process.model.ProcessNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * A simple node executor that logs the node execution.
 * This is a sample implementation of the ProcessNodeExecutor interface.
 */
@Component
public class LoggingNodeExecutor implements ProcessNodeExecutor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingNodeExecutor.class);
    
    @Override
    public boolean canExecute(String nodeType) {
        return "logging".equals(nodeType);
    }
    
    @Override
    public CompletableFuture<Object> execute(ProcessNode node) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Executing logging node: {}", node.getName());
            String message = node.getProperties().getOrDefault("message", "No message provided");
            logger.info("Node message: {}", message);
            
            // Simulate some processing time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Node execution interrupted", e);
                node.setStatus(ProcessNode.ProcessNodeStatus.FAILED);
                return "Execution failed: " + e.getMessage();
            }
            
            node.setStatus(ProcessNode.ProcessNodeStatus.COMPLETED);
            return "Logged message: " + message;
        });
    }
}
