package com.example.mcp.controller;

import com.example.mcp.service.DatabaseService;
import com.example.mcp.service.PromptService;
import com.example.mcp.service.RpcService;
import com.example.mcp.model.DatabaseEntity;
import com.example.mcp.model.Prompt;
import com.example.mcp.model.RpcRequest;
import com.example.mcp.model.RpcResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private final DatabaseService databaseService;
    private final RpcService rpcService;
    private final PromptService promptService;

    @Autowired
    public McpController(
            DatabaseService databaseService,
            RpcService rpcService,
            PromptService promptService) {
        this.databaseService = databaseService;
        this.rpcService = rpcService;
        this.promptService = promptService;
    }

    // Database Resource Endpoints
    @GetMapping("/database/entities")
    public ResponseEntity<List<DatabaseEntity>> getAllEntities() {
        return ResponseEntity.ok(databaseService.getAll());
    }

    @GetMapping("/database/entities/{id}")
    public ResponseEntity<DatabaseEntity> getEntityById(@PathVariable Long id) {
        DatabaseEntity entity = databaseService.getById(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(entity);
    }

    // RPC Tool Endpoints
    @PostMapping("/rpc/call")
    public ResponseEntity<RpcResponse> callRpcService(@RequestBody RpcRequest request) {
        RpcResponse response = rpcService.callExternalService(request);
        return ResponseEntity.ok(response);
    }

    // Prompt Endpoints
    @GetMapping("/prompts")
    public ResponseEntity<List<Prompt>> getAllPrompts() {
        return ResponseEntity.ok(promptService.getAll());
    }

    @GetMapping("/prompts/{id}")
    public ResponseEntity<Prompt> getPromptById(@PathVariable Long id) {
        Prompt prompt = promptService.getById(id);
        if (prompt == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prompt);
    }

    @PostMapping("/prompts/{id}/generate")
    public ResponseEntity<Map<String, Object>> generatePrompt(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> context) {
        
        Prompt prompt = promptService.getById(id);
        if (prompt == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contextValue = context != null && context.containsKey("context") 
                ? context.get("context") 
                : "";
        
        String content = prompt.getContent();
        if (!contextValue.isEmpty()) {
            content = content.replace("{{context}}", contextValue);
        }
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "system");
        message.put("content", content);
        
        response.put("messages", List.of(message));
        return ResponseEntity.ok(response);
    }
}
