package com.example.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.example.mcp.mcp.server.McpSyncServer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class McpServerConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testMcpServerConfiguration() {
        // Verify that the MCP server is properly configured
        McpSyncServer mcpServer = context.getBean(McpSyncServer.class);
        assertNotNull(mcpServer, "MCP server should be configured");
    }
}
