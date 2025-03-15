package com.orchestration.process.core.executor.math;

import org.springframework.stereotype.Component;

/**
 * Node executor for multiplication operations.
 */
@Component
public class MultiplicationNodeExecutor extends AbstractMathNodeExecutor {
    
    @Override
    public boolean canExecute(String nodeType) {
        return "math.multiplication".equals(nodeType);
    }
    
    @Override
    protected double performOperation(double leftOperand, double rightOperand) {
        return leftOperand * rightOperand;
    }
}
