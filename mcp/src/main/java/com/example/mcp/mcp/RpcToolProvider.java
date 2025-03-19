package com.example.mcp.mcp;

import com.example.mcp.model.RpcRequest;
import com.example.mcp.model.RpcResponse;
import com.example.mcp.service.RpcService;
import com.example.mcp.mcp.server.McpServerFeatures;
import com.example.mcp.mcp.schema.McpSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class RpcToolProvider {

    private final RpcService rpcService;

    @Autowired
    public RpcToolProvider(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    public McpServerFeatures.SyncToolRegistration getRpcTool() {
        // Define the tool schema
        String schema = "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"endpoint\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"description\": \"The endpoint URL to call\"\n" +
                "    },\n" +
                "    \"method\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"enum\": [\"GET\", \"POST\", \"PUT\", \"DELETE\"],\n" +
                "      \"description\": \"The HTTP method to use\"\n" +
                "    },\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"description\": \"The parameters to send with the request\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\"endpoint\", \"method\"]\n" +
                "}";

        // Define the tool
        McpSchema.Tool tool = new McpSchema.Tool(
                "rpc-call",
                "Call an external RPC service",
                schema
        );

        // Define the handler function
        Function<Map<String, Object>, McpSchema.CallToolResult> handler = args -> {
            try {
                // Create an RPC request from the arguments
                RpcRequest request = new RpcRequest();
                request.setEndpoint((String) args.get("endpoint"));
                request.setMethod((String) args.get("method"));
                
                @SuppressWarnings("unchecked")
                Map<String, Object> parameters = (Map<String, Object>) args.get("parameters");
                request.setParameters(parameters != null ? parameters : new HashMap<>());
                
                // Call the RPC service
                RpcResponse response = rpcService.callExternalService(request);
                
                // Return the result
                if (response.isSuccess()) {
                    return new McpSchema.CallToolResult(response.getData());
                } else {
                    return new McpSchema.CallToolResult(
                            new McpSchema.Error("RPC_ERROR", response.getErrorMessage())
                    );
                }
            } catch (Exception e) {
                return new McpSchema.CallToolResult(
                        new McpSchema.Error("INTERNAL_ERROR", "Error calling RPC service: " + e.getMessage())
                );
            }
        };

        // Create and return the tool registration
        return new McpServerFeatures.SyncToolRegistration(tool, handler);
    }
}
