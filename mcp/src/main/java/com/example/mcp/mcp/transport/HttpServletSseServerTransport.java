package com.example.mcp.mcp.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * HTTP Servlet Server-Sent Events transport for MCP
 */
public class HttpServletSseServerTransport implements ServerMcpTransport {
    
    private final ObjectMapper objectMapper;
    private final String path;
    
    public HttpServletSseServerTransport(ObjectMapper objectMapper, String path) {
        this.objectMapper = objectMapper;
        this.path = path;
    }
    
    @Override
    public String getPath() {
        return path;
    }
}
