package com.orchestration.process.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ProcessNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nodeId;
    private String name;
    private String type;
    private Integer level;
    
    @ElementCollection
    @CollectionTable(name = "node_properties", joinColumns = @JoinColumn(name = "node_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> properties = new ConcurrentHashMap<>();
    
    @ElementCollection
    private List<String> childNodeIds = new ArrayList<>();
    
    @Transient
    private ProcessNodeStatus status = ProcessNodeStatus.PENDING;
    
    @Transient
    private Object result;
    
    public enum ProcessNodeStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
