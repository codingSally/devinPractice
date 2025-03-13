package com.orchestration.process.core.impl;

import com.orchestration.process.core.ProcessNodeExecutor;
import com.orchestration.process.core.ProcessNodeExecutorRegistry;
import com.orchestration.process.core.ProcessOrchestrator;
import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.model.ProcessNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Implementation of ProcessOrchestrator that uses breadth-first traversal
 * and parallel execution for nodes at the same level.
 */
@Component
public class BfsProcessOrchestrator implements ProcessOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(BfsProcessOrchestrator.class);
    
    private final ProcessNodeExecutorRegistry executorRegistry;
    private final Map<Long, ProcessDefinition> runningProcesses = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> stopRequests = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    @Autowired
    public BfsProcessOrchestrator(ProcessNodeExecutorRegistry executorRegistry) {
        this.executorRegistry = executorRegistry;
    }
    
    @Override
    public CompletableFuture<ProcessDefinition> execute(ProcessDefinition processDefinition) {
        // Store the process definition for status tracking
        runningProcesses.put(processDefinition.getId(), processDefinition);
        stopRequests.put(processDefinition.getId(), false);
        
        // Build the node map if not already built
        if (processDefinition.getNodeMap() == null || processDefinition.getNodeMap().isEmpty()) {
            processDefinition.buildNodeMap();
        }
        
        // Start the execution
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting execution of process: {}", processDefinition.getName());
                
                // Get the root nodes
                List<ProcessNode> rootNodes = processDefinition.getRootNodeIds().stream()
                        .map(processDefinition::getNodeById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                
                if (rootNodes.isEmpty()) {
                    logger.warn("No root nodes found for process: {}", processDefinition.getName());
                    return processDefinition;
                }
                
                // Initialize the queue with root nodes
                Queue<List<ProcessNode>> levelQueue = new LinkedList<>();
                levelQueue.add(rootNodes);
                
                // Process each level
                while (!levelQueue.isEmpty() && !stopRequests.get(processDefinition.getId())) {
                    List<ProcessNode> currentLevel = levelQueue.poll();
                    
                    // Execute all nodes at the current level in parallel
                    List<CompletableFuture<Object>> futures = new ArrayList<>();
                    
                    for (ProcessNode node : currentLevel) {
                        // Skip already completed or failed nodes
                        if (node.getStatus() == ProcessNode.ProcessNodeStatus.COMPLETED ||
                            node.getStatus() == ProcessNode.ProcessNodeStatus.FAILED) {
                            continue;
                        }
                        
                        // Find an executor for the node
                        Optional<ProcessNodeExecutor> executorOpt = executorRegistry.findExecutor(node.getType());
                        
                        if (executorOpt.isPresent()) {
                            ProcessNodeExecutor executor = executorOpt.get();
                            node.setStatus(ProcessNode.ProcessNodeStatus.RUNNING);
                            
                            // Execute the node
                            CompletableFuture<Object> future = executor.execute(node);
                            futures.add(future);
                        } else {
                            logger.error("No executor found for node type: {}", node.getType());
                            node.setStatus(ProcessNode.ProcessNodeStatus.FAILED);
                        }
                    }
                    
                    // Wait for all nodes at this level to complete
                    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0])
                    );
                    
                    try {
                        allFutures.get(); // Wait for all nodes to complete
                    } catch (Exception e) {
                        logger.error("Error waiting for node execution", e);
                    }
                    
                    // Check if all nodes at this level completed successfully
                    boolean allCompleted = currentLevel.stream()
                            .allMatch(node -> node.getStatus() == ProcessNode.ProcessNodeStatus.COMPLETED);
                    
                    if (!allCompleted) {
                        logger.warn("Not all nodes at level completed successfully");
                        // We still continue to the next level, but log the warning
                    }
                    
                    // Collect child nodes for the next level
                    List<ProcessNode> nextLevel = new ArrayList<>();
                    
                    for (ProcessNode node : currentLevel) {
                        if (node.getStatus() == ProcessNode.ProcessNodeStatus.COMPLETED) {
                            // Add child nodes to the next level
                            for (String childId : node.getChildNodeIds()) {
                                ProcessNode childNode = processDefinition.getNodeById(childId);
                                if (childNode != null) {
                                    nextLevel.add(childNode);
                                }
                            }
                        }
                    }
                    
                    // Add the next level to the queue if not empty
                    if (!nextLevel.isEmpty()) {
                        levelQueue.add(nextLevel);
                    }
                }
                
                logger.info("Process execution completed: {}", processDefinition.getName());
                return processDefinition;
            } finally {
                // Clean up
                runningProcesses.remove(processDefinition.getId());
                stopRequests.remove(processDefinition.getId());
            }
        }, executorService);
    }
    
    @Override
    public ProcessDefinition getStatus(Long processId) {
        return runningProcesses.get(processId);
    }
    
    @Override
    public boolean stopProcess(Long processId) {
        if (runningProcesses.containsKey(processId)) {
            stopRequests.put(processId, true);
            return true;
        }
        return false;
    }
}
