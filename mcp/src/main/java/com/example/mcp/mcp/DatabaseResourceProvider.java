package com.example.mcp.mcp;

import com.example.mcp.model.DatabaseEntity;
import com.example.mcp.service.DatabaseService;
import com.example.mcp.mcp.server.McpServerFeatures;
import com.example.mcp.mcp.schema.McpSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DatabaseResourceProvider {

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseResourceProvider(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public McpServerFeatures.SyncResourceRegistration getDatabaseResource() {
        // Define the resource
        McpSchema.Resource resource = new McpSchema.Resource(
                "database://entities",
                "Database entities resource",
                "application/json",
                "Provides access to database entities",
                null
        );

        // Define the handler function
        Function<McpSchema.GetResourceRequest, McpSchema.GetResourceResult> handler = request -> {
            // Get all entities from the database
            List<DatabaseEntity> entities = databaseService.getAll();
            
            // Convert entities to a format suitable for MCP
            List<Map<String, Object>> entityMaps = entities.stream()
                    .map(entity -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", entity.getId());
                        map.put("name", entity.getName());
                        map.put("value", entity.getValue());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            // Return the result
            return new McpSchema.GetResourceResult(entityMaps);
        };

        // Create and return the resource registration
        return new McpServerFeatures.SyncResourceRegistration(resource, handler);
    }

    public McpServerFeatures.SyncResourceRegistration getDatabaseEntityResource() {
        // Define the resource
        McpSchema.Resource resource = new McpSchema.Resource(
                "database://entity/{id}",
                "Database entity by ID",
                "application/json",
                "Provides access to a specific database entity by ID",
                null
        );

        // Define the handler function
        Function<McpSchema.GetResourceRequest, McpSchema.GetResourceResult> handler = request -> {
            // Extract the ID from the request
            String idStr = request.getUri().split("/")[3];
            Long id = Long.parseLong(idStr);
            
            // Get the entity from the database
            DatabaseEntity entity = databaseService.getById(id);
            
            if (entity == null) {
                return new McpSchema.GetResourceResult(
                        new McpSchema.Error("NOT_FOUND", "Entity not found with ID: " + id)
                );
            }
            
            // Convert entity to a map
            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("id", entity.getId());
            entityMap.put("name", entity.getName());
            entityMap.put("value", entity.getValue());
            
            // Return the result
            return new McpSchema.GetResourceResult(entityMap);
        };

        // Create and return the resource registration
        return new McpServerFeatures.SyncResourceRegistration(resource, handler);
    }
}
