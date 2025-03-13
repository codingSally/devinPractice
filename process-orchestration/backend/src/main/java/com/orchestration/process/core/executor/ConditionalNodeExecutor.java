package com.orchestration.process.core.executor;

import com.orchestration.process.core.ProcessNodeExecutor;
import com.orchestration.process.model.ProcessNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.CompletableFuture;

/**
 * A node executor that evaluates a condition and determines which child nodes to execute.
 */
@Component
public class ConditionalNodeExecutor implements ProcessNodeExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ConditionalNodeExecutor.class);
    private final ScriptEngine engine;
    
    public ConditionalNodeExecutor() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("JavaScript");
    }
    
    @Override
    public boolean canExecute(String nodeType) {
        return "conditional".equals(nodeType);
    }
    
    @Override
    public CompletableFuture<Object> execute(ProcessNode node) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Executing conditional node: {}", node.getName());
                
                String condition = node.getProperties().getOrDefault("condition", "");
                
                if (condition.isEmpty()) {
                    throw new IllegalArgumentException("Condition is required for conditional node");
                }
                
                Object result = engine.eval(condition);
                boolean conditionResult = false;
                
                if (result instanceof Boolean) {
                    conditionResult = (Boolean) result;
                } else if (result instanceof Number) {
                    conditionResult = ((Number) result).doubleValue() != 0;
                } else if (result instanceof String) {
                    conditionResult = !((String) result).isEmpty();
                }
                
                // Store the result
                node.setResult(conditionResult);
                node.setStatus(ProcessNode.ProcessNodeStatus.COMPLETED);
                
                return conditionResult;
            } catch (ScriptException e) {
                logger.error("Condition evaluation failed", e);
                node.setStatus(ProcessNode.ProcessNodeStatus.FAILED);
                return "Condition evaluation failed: " + e.getMessage();
            }
        });
    }
}
