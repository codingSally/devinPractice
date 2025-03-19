package com.example.mcp.mcp.server;

/**
 * Interface for a synchronous MCP server
 */
public interface McpSyncServer {
    
    /**
     * Start the MCP server
     */
    void start();
    
    /**
     * Stop the MCP server
     */
    void stop();
}
