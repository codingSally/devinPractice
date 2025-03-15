package com.orchestration.process.core.executor.math;

import org.springframework.stereotype.Component;

/**
 * Node executor for division operations.
 */
@Component
public class DivisionNodeExecutor extends AbstractMathNodeExecutor {
    
    @Override
    public boolean canExecute(String nodeType) {
        return "math.division".equals(nodeType);
    }
    
    @Override
    protected double performOperation(double leftOperand, double rightOperand) {
        if (rightOperand == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return leftOperand / rightOperand;
    }
}
