package com.orchestration.process.core.executor;

import com.orchestration.process.core.ProcessNodeExecutor;
import com.orchestration.process.model.ProcessNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A node executor that makes HTTP requests.
 */
@Component
public class HttpNodeExecutor implements ProcessNodeExecutor {
    private static final Logger logger = LoggerFactory.getLogger(HttpNodeExecutor.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public boolean canExecute(String nodeType) {
        return "http".equals(nodeType);
    }
    
    @Override
    public CompletableFuture<Object> execute(ProcessNode node) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Executing HTTP node: {}", node.getName());
                
                String url = node.getProperties().getOrDefault("url", "");
                String method = node.getProperties().getOrDefault("method", "GET");
                String body = node.getProperties().getOrDefault("body", "");
                
                if (url.isEmpty()) {
                    throw new IllegalArgumentException("URL is required for HTTP node");
                }
                
                HttpHeaders headers = new HttpHeaders();
                node.getProperties().entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith("header."))
                        .forEach(entry -> headers.add(
                                entry.getKey().substring("header.".length()),
                                entry.getValue()
                        ));
                
                HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.valueOf(method),
                        requestEntity,
                        String.class
                );
                
                Map<String, Object> result = new HashMap<>();
                result.put("statusCode", response.getStatusCodeValue());
                result.put("body", response.getBody());
                result.put("headers", response.getHeaders());
                
                node.setStatus(ProcessNode.ProcessNodeStatus.COMPLETED);
                return result;
            } catch (Exception e) {
                logger.error("HTTP node execution failed", e);
                node.setStatus(ProcessNode.ProcessNodeStatus.FAILED);
                return "Execution failed: " + e.getMessage();
            }
        });
    }
}
