package com.example.mcp.service;

import com.example.mcp.model.RpcRequest;
import com.example.mcp.model.RpcResponse;

public interface RpcService {
    RpcResponse callExternalService(RpcRequest request);
}
