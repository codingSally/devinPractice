package com.orchestration.process.core.executor.math;

import org.springframework.stereotype.Component;

/**
 * Node executor for subtraction operations.
 */
@Component
public class SubtractionNodeExecutor extends AbstractMathNodeExecutor {
    
    @Override
    public boolean canExecute(String nodeType) {
        return "math.subtraction".equals(nodeType);
    }
    
    @Override
    protected double performOperation(double leftOperand, double rightOperand) {
        return leftOperand - rightOperand;
    }
}
