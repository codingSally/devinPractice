package com.example.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import java.util.logging.Logger;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class McpApplication {
    private static final Logger logger = Logger.getLogger(McpApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(McpApplication.class, args);
        logger.info("MCP Application started successfully");
    }
}
