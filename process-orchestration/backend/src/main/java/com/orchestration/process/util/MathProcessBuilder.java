package com.orchestration.process.util;

import com.orchestration.process.model.ProcessDefinition;
import com.orchestration.process.model.ProcessNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for building mathematical process definitions.
 */
public class MathProcessBuilder {
    
    /**
     * Build a process definition for the example: 10 * (1*3 + 2*3 + 3*5 + 4*6) / 2
     * @return the process definition
     */
    public static ProcessDefinition buildExampleProcess() {
        List<ProcessNode> nodes = new ArrayList<>();
        List<String> rootNodeIds = new ArrayList<>();
        
        // Layer 3: Individual multiplication operations
        ProcessNode mult1 = createMathNode("math.multiplication", "1*3", 2);
        mult1.getProperties().put("leftOperand", "1");
        mult1.getProperties().put("rightOperand", "3");
        nodes.add(mult1);
        
        ProcessNode mult2 = createMathNode("math.multiplication", "2*3", 2);
        mult2.getProperties().put("leftOperand", "2");
        mult2.getProperties().put("rightOperand", "3");
        nodes.add(mult2);
        
        ProcessNode mult3 = createMathNode("math.multiplication", "3*5", 2);
        mult3.getProperties().put("leftOperand", "3");
        mult3.getProperties().put("rightOperand", "5");
        nodes.add(mult3);
        
        ProcessNode mult4 = createMathNode("math.multiplication", "4*6", 2);
        mult4.getProperties().put("leftOperand", "4");
        mult4.getProperties().put("rightOperand", "6");
        nodes.add(mult4);
        
        // Layer 2: Addition operations
        ProcessNode add1 = createMathNode("math.addition", "(1*3)+(2*3)", 1);
        add1.getProperties().put("leftNodeResult", mult1.getNodeId());
        add1.getProperties().put("rightNodeResult", mult2.getNodeId());
        add1.getChildNodeIds().add(mult1.getNodeId());
        add1.getChildNodeIds().add(mult2.getNodeId());
        nodes.add(add1);
        
        ProcessNode add2 = createMathNode("math.addition", "(3*5)+(4*6)", 1);
        add2.getProperties().put("leftNodeResult", mult3.getNodeId());
        add2.getProperties().put("rightNodeResult", mult4.getNodeId());
        add2.getChildNodeIds().add(mult3.getNodeId());
        add2.getChildNodeIds().add(mult4.getNodeId());
        nodes.add(add2);
        
        ProcessNode addFinal = createMathNode("math.addition", "sum", 1);
        addFinal.getProperties().put("leftNodeResult", add1.getNodeId());
        addFinal.getProperties().put("rightNodeResult", add2.getNodeId());
        addFinal.getChildNodeIds().add(add1.getNodeId());
        addFinal.getChildNodeIds().add(add2.getNodeId());
        nodes.add(addFinal);
        
        // Layer 1: Multiplication and division operations
        ProcessNode mult5 = createMathNode("math.multiplication", "10*sum", 0);
        mult5.getProperties().put("leftOperand", "10");
        mult5.getProperties().put("rightNodeResult", addFinal.getNodeId());
        mult5.getChildNodeIds().add(addFinal.getNodeId());
        nodes.add(mult5);
        
        ProcessNode div1 = createMathNode("math.division", "result/2", 0);
        div1.getProperties().put("leftNodeResult", mult5.getNodeId());
        div1.getProperties().put("rightOperand", "2");
        div1.getChildNodeIds().add(mult5.getNodeId());
        nodes.add(div1);
        
        rootNodeIds.add(div1.getNodeId());
        
        // Create the process definition
        ProcessDefinition processDefinition = ProcessDefinition.builder()
                .name("Mathematical Example")
                .description("10 * (1*3 + 2*3 + 3*5 + 4*6) / 2")
                .nodes(nodes)
                .rootNodeIds(rootNodeIds)
                .build();
        
        processDefinition.buildNodeMap();
        return processDefinition;
    }
    
    /**
     * Create a math node with the given type and name
     * @param type the node type
     * @param name the node name
     * @param level the node level
     * @return the created node
     */
    private static ProcessNode createMathNode(String type, String name, Integer level) {
        return ProcessNode.builder()
                .nodeId(UUID.randomUUID().toString())
                .name(name)
                .type(type)
                .level(level)
                .properties(new HashMap<>())
                .childNodeIds(new ArrayList<>())
                .build();
    }
}
