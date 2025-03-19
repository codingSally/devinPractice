package com.example.mcp.mcp.server;

import com.example.mcp.mcp.schema.McpSchema;

import java.util.Map;
import java.util.function.Function;
import java.util.List;

/**
 * Local implementation of MCP Server Features
 */
public class McpServerFeatures {

    /**
     * Registration for a synchronous resource
     */
    public static class SyncResourceRegistration {
        private final McpSchema.Resource resource;
        private final Function<McpSchema.GetResourceRequest, McpSchema.GetResourceResult> handler;

        public SyncResourceRegistration(McpSchema.Resource resource, 
                                       Function<McpSchema.GetResourceRequest, McpSchema.GetResourceResult> handler) {
            this.resource = resource;
            this.handler = handler;
        }

        public McpSchema.Resource getResource() {
            return resource;
        }

        public Function<McpSchema.GetResourceRequest, McpSchema.GetResourceResult> getHandler() {
            return handler;
        }
    }

    /**
     * Registration for a synchronous tool
     */
    public static class SyncToolRegistration {
        private final McpSchema.Tool tool;
        private final Function<Map<String, Object>, McpSchema.CallToolResult> handler;

        public SyncToolRegistration(McpSchema.Tool tool, 
                                   Function<Map<String, Object>, McpSchema.CallToolResult> handler) {
            this.tool = tool;
            this.handler = handler;
        }

        public McpSchema.Tool getTool() {
            return tool;
        }

        public Function<Map<String, Object>, McpSchema.CallToolResult> getHandler() {
            return handler;
        }
    }

    /**
     * Registration for a synchronous prompt
     */
    public static class SyncPromptRegistration {
        private final McpSchema.Prompt prompt;
        private final Function<McpSchema.GetPromptRequest, McpSchema.GetPromptResult> handler;

        public SyncPromptRegistration(McpSchema.Prompt prompt, 
                                     Function<McpSchema.GetPromptRequest, McpSchema.GetPromptResult> handler) {
            this.prompt = prompt;
            this.handler = handler;
        }

        public McpSchema.Prompt getPrompt() {
            return prompt;
        }

        public Function<McpSchema.GetPromptRequest, McpSchema.GetPromptResult> getHandler() {
            return handler;
        }
    }
}
