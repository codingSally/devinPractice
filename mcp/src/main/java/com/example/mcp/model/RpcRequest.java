package com.example.mcp.model;

import lombok.Data;
import java.util.Map;

@Data
public class RpcRequest {
    private String endpoint;
    private String method;
    private Map<String, Object> parameters;
}
