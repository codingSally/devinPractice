package com.example.mcp.service.impl;

import com.example.mcp.model.RpcRequest;
import com.example.mcp.model.RpcResponse;
import com.example.mcp.service.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RpcServiceImpl implements RpcService {
    private static final Logger logger = Logger.getLogger(RpcServiceImpl.class.getName());

    private final RestTemplate restTemplate;

    @Autowired
    public RpcServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public RpcResponse callExternalService(RpcRequest request) {
        logger.info("Calling external RPC service: " + request.getEndpoint());
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<Object> httpEntity = new HttpEntity<>(request.getParameters(), headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                request.getEndpoint(),
                HttpMethod.valueOf(request.getMethod()),
                httpEntity,
                Object.class
            );
            
            return RpcResponse.success(response.getBody());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calling external RPC service", e);
            return RpcResponse.error("Failed to call external service: " + e.getMessage());
        }
    }
}
