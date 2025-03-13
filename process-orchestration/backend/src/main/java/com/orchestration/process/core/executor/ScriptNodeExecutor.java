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
 * A node executor that executes JavaScript code.
 */
@Component
public class ScriptNodeExecutor implements ProcessNodeExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ScriptNodeExecutor.class);
    private final ScriptEngine engine;
    
    public ScriptNodeExecutor() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("JavaScript");
    }
    
    @Override
    public boolean canExecute(String nodeType) {
        return "script".equals(nodeType);
    }
    
    @Override
    public CompletableFuture<Object> execute(ProcessNode node) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Executing script node: {}", node.getName());
                
                String script = node.getProperties().getOrDefault("script", "");
                
                if (script.isEmpty()) {
                    throw new IllegalArgumentException("Script is required for script node");
                }
                
                Object result = engine.eval(script);
                node.setStatus(ProcessNode.ProcessNodeStatus.COMPLETED);
                return result;
            } catch (ScriptException e) {
                logger.error("Script execution failed", e);
                node.setStatus(ProcessNode.ProcessNodeStatus.FAILED);
                return "Script execution failed: " + e.getMessage();
            }
        });
    }
}
