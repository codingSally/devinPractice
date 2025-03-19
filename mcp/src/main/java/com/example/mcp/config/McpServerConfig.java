package com.example.mcp.config;

import com.example.mcp.mcp.DatabaseResourceProvider;
import com.example.mcp.mcp.PromptProvider;
import com.example.mcp.mcp.RpcToolProvider;
import com.example.mcp.mcp.schema.McpSchema;
import com.example.mcp.mcp.server.McpServer;
import com.example.mcp.mcp.server.McpSyncServer;
import com.example.mcp.mcp.transport.HttpServletSseServerTransport;
import com.example.mcp.mcp.transport.ServerMcpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class McpServerConfig {

    @Value("${mcp.server.path:/api/mcp/connect}")
    private String mcpServerPath;

    @Bean
    @org.springframework.context.annotation.Primary
    public ServerMcpTransport mcpTransport(ObjectMapper objectMapper) {
        return new HttpServletSseServerTransport(objectMapper, mcpServerPath);
    }

    @Bean
    public McpSyncServer mcpSyncServer(
            ServerMcpTransport transport,
            DatabaseResourceProvider databaseResourceProvider,
            RpcToolProvider rpcToolProvider,
            PromptProvider promptProvider) {
        
        // Configure server capabilities
        McpSchema.ServerCapabilities capabilities = McpSchema.ServerCapabilities.builder()
                .resources(true, false)  // Enable resources, no streaming
                .tools(false)            // Enable tools, no streaming
                .prompts(false)          // Enable prompts, no streaming
                .build();
        
        // Build the MCP server
        return McpServer.using(transport)
                .serverInfo("MCP Server", "1.0.0")
                .capabilities(capabilities)
                .resources(databaseResourceProvider.getDatabaseResource(), 
                          databaseResourceProvider.getDatabaseEntityResource())
                .tools(rpcToolProvider.getRpcTool())
                .prompts(promptProvider.getAllPrompts().toArray(new com.example.mcp.mcp.server.McpServerFeatures.SyncPromptRegistration[0]))
                .sync();
    }
}
