package com.example.mcp.mcp.server;

import com.example.mcp.mcp.schema.McpSchema;
import com.example.mcp.mcp.transport.ServerMcpTransport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Local implementation of MCP Server
 */
public class McpServer {
    
    private final ServerMcpTransport transport;
    private String name = "MCP Server";
    private String version = "1.0.0";
    private McpSchema.ServerCapabilities capabilities;
    private final List<McpServerFeatures.SyncResourceRegistration> resources = new ArrayList<>();
    private final List<McpServerFeatures.SyncToolRegistration> tools = new ArrayList<>();
    private final List<McpServerFeatures.SyncPromptRegistration> prompts = new ArrayList<>();

    private McpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public static McpServer using(ServerMcpTransport transport) {
        return new McpServer(transport);
    }

    public McpServer serverInfo(String name, String version) {
        this.name = name;
        this.version = version;
        return this;
    }

    public McpServer capabilities(McpSchema.ServerCapabilities capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    public McpServer resources(McpServerFeatures.SyncResourceRegistration... registrations) {
        this.resources.addAll(Arrays.asList(registrations));
        return this;
    }

    public McpServer tools(McpServerFeatures.SyncToolRegistration... registrations) {
        this.tools.addAll(Arrays.asList(registrations));
        return this;
    }

    public McpServer prompts(McpServerFeatures.SyncPromptRegistration... registrations) {
        this.prompts.addAll(Arrays.asList(registrations));
        return this;
    }

    public McpSyncServer sync() {
        return new McpSyncServerImpl(transport, name, version, capabilities, resources, tools, prompts);
    }

    private static class McpSyncServerImpl implements McpSyncServer {
        private final ServerMcpTransport transport;
        private final String name;
        private final String version;
        private final McpSchema.ServerCapabilities capabilities;
        private final List<McpServerFeatures.SyncResourceRegistration> resources;
        private final List<McpServerFeatures.SyncToolRegistration> tools;
        private final List<McpServerFeatures.SyncPromptRegistration> prompts;

        public McpSyncServerImpl(ServerMcpTransport transport, String name, String version,
                                McpSchema.ServerCapabilities capabilities,
                                List<McpServerFeatures.SyncResourceRegistration> resources,
                                List<McpServerFeatures.SyncToolRegistration> tools,
                                List<McpServerFeatures.SyncPromptRegistration> prompts) {
            this.transport = transport;
            this.name = name;
            this.version = version;
            this.capabilities = capabilities;
            this.resources = resources;
            this.tools = tools;
            this.prompts = prompts;
        }

        @Override
        public void start() {
            // In a real implementation, this would set up the transport and start listening
            System.out.println("MCP Server started with " + resources.size() + " resources, " 
                              + tools.size() + " tools, and " + prompts.size() + " prompts");
        }

        @Override
        public void stop() {
            // In a real implementation, this would stop the transport
            System.out.println("MCP Server stopped");
        }
    }
}
