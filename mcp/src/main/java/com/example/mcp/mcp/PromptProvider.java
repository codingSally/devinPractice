package com.example.mcp.mcp;

import com.example.mcp.model.Prompt;
import com.example.mcp.service.PromptService;
import com.example.mcp.mcp.server.McpServerFeatures;
import com.example.mcp.mcp.schema.McpSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PromptProvider {

    private final PromptService promptService;

    @Autowired
    public PromptProvider(PromptService promptService) {
        this.promptService = promptService;
    }

    public List<McpServerFeatures.SyncPromptRegistration> getAllPrompts() {
        // Get all prompts from the service
        List<Prompt> prompts = promptService.getAll();
        
        // Convert each prompt to a prompt registration
        return prompts.stream()
                .map(this::createPromptRegistration)
                .collect(Collectors.toList());
    }

    private McpServerFeatures.SyncPromptRegistration createPromptRegistration(Prompt prompt) {
        // Define the prompt schema
        String schema = "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"context\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"description\": \"Additional context for the prompt\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Create the MCP prompt
        McpSchema.Prompt mcpPrompt = new McpSchema.Prompt(
                prompt.getName(),
                prompt.getCategory() + " prompt: " + prompt.getName(),
                schema
        );

        // Define the handler function
        Function<McpSchema.GetPromptRequest, McpSchema.GetPromptResult> handler = request -> {
            try {
                // Get the context from the request
                Map<String, Object> args = request.getArguments();
                String context = args != null && args.containsKey("context") 
                        ? (String) args.get("context") 
                        : "";
                
                // Create the prompt content with the context
                String content = prompt.getContent();
                if (!context.isEmpty()) {
                    content = content.replace("{{context}}", context);
                }
                
                // Create the messages
                List<McpSchema.PromptMessage> messages = new ArrayList<>();
                messages.add(new McpSchema.PromptMessage(
                        McpSchema.Role.SYSTEM,
                        new McpSchema.TextContent(content)
                ));
                
                // Return the result
                return new McpSchema.GetPromptResult(messages);
            } catch (Exception e) {
                return new McpSchema.GetPromptResult(
                        new McpSchema.Error("PROMPT_ERROR", "Error generating prompt: " + e.getMessage())
                );
            }
        };

        // Create and return the prompt registration
        return new McpServerFeatures.SyncPromptRegistration(mcpPrompt, handler);
    }
}
