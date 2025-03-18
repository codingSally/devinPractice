package com.example.mcp.controller;

import com.example.mcp.model.RpcRequest;
import com.example.mcp.model.RpcResponse;
import com.example.mcp.service.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rpc")
public class RpcController {
    private static final Logger logger = Logger.getLogger(RpcController.class.getName());

    private final RpcService rpcService;

    @Autowired
    public RpcController(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @PostMapping("/call")
    public ResponseEntity<RpcResponse> callExternalService(@RequestBody RpcRequest request) {
        logger.info("REST request to call external RPC service: " + request.getEndpoint());
        RpcResponse response = rpcService.callExternalService(request);
        return ResponseEntity.ok(response);
    }
}
