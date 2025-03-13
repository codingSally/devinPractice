package com.orchestration.process.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ProcessDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProcessNode> nodes = new ArrayList<>();
    
    @ElementCollection
    private List<String> rootNodeIds = new ArrayList<>();
    
    @Transient
    private Map<String, ProcessNode> nodeMap = new HashMap<>();
    
    public void buildNodeMap() {
        nodeMap.clear();
        for (ProcessNode node : nodes) {
            nodeMap.put(node.getNodeId(), node);
        }
    }
    
    public ProcessNode getNodeById(String nodeId) {
        return nodeMap.getOrDefault(nodeId, null);
    }
}
