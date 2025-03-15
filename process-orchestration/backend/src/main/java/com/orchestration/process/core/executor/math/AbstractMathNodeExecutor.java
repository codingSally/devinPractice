package com.orchestration.process.core.executor.math;

import com.orchestration.process.core.ProcessNodeExecutor;
import com.orchestration.process.model.ProcessNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Base class for mathematical operation node executors.
 */
public abstract class AbstractMathNodeExecutor implements ProcessNodeExecutor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMathNodeExecutor.class);
    
    @Override
    public CompletableFuture<Object> execute(ProcessNode node) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Executing math node: {}", node.getName());
                
                // Get operands from node properties
                String leftOperandStr = node.getProperties().getOrDefault("leftOperand", "0");
                String rightOperandStr = node.getProperties().getOrDefault("rightOperand", "0");
                
                double leftOperand, rightOperand;
                
                // Try to parse operands as numbers
                try {
                    leftOperand = Double.parseDouble(leftOperandStr);
                } catch (NumberFormatException e) {
                    // If not a number, it might be a reference to another node's result
                    Object leftResult = node.getProperties().get("leftNodeResult");
                    if (leftResult instanceof Number) {
                        leftOperand = ((Number) leftResult).doubleValue();
                    } else {
                        throw new IllegalArgumentException("Left operand is not a valid number: " + leftOperandStr);
                    }
                }
                
                try {
                    rightOperand = Double.parseDouble(rightOperandStr);
                } catch (NumberFormatException e) {
                    // If not a number, it might be a reference to another node's result
                    Object rightResult = node.getProperties().get("rightNodeResult");
                    if (rightResult instanceof Number) {
                        rightOperand = ((Number) rightResult).doubleValue();
                    } else {
                        throw new IllegalArgumentException("Right operand is not a valid number: " + rightOperandStr);
                    }
                }
                
                // Perform the operation
                double result = performOperation(leftOperand, rightOperand);
                
                // Set the result and status
                node.setResult(result);
                node.setStatus(ProcessNode.ProcessNodeStatus.COMPLETED);
                
                return result;
            } catch (Exception e) {
                logger.error("Math operation failed", e);
                node.setStatus(ProcessNode.ProcessNodeStatus.FAILED);
                return "Operation failed: " + e.getMessage();
            }
        });
    }
    
    /**
     * Perform the specific mathematical operation
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     * @return the result of the operation
     */
    protected abstract double performOperation(double leftOperand, double rightOperand);
}
