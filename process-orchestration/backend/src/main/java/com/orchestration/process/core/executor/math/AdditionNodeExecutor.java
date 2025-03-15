package com.orchestration.process.core.executor.math;

import org.springframework.stereotype.Component;

/**
 * Node executor for addition operations.
 */
@Component
public class AdditionNodeExecutor extends AbstractMathNodeExecutor {
    
    @Override
    public boolean canExecute(String nodeType) {
        return "math.addition".equals(nodeType);
    }
    
    @Override
    protected double performOperation(double leftOperand, double rightOperand) {
        return leftOperand + rightOperand;
    }
}
