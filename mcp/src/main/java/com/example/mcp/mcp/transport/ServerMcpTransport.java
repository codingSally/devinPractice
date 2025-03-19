package com.example.mcp.mcp.transport;

/**
 * Interface for MCP server transport
 */
public interface ServerMcpTransport {
    
    /**
     * Get the path where the transport is listening
     * 
     * @return the path
     */
    String getPath();
}
