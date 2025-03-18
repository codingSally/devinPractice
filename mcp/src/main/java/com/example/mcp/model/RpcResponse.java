package com.example.mcp.model;

import lombok.Data;

@Data
public class RpcResponse {
    private boolean success;
    private Object data;
    private String errorMessage;
    
    public static RpcResponse success(Object data) {
        RpcResponse response = new RpcResponse();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    
    public static RpcResponse error(String errorMessage) {
        RpcResponse response = new RpcResponse();
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
