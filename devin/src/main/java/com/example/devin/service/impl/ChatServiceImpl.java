package com.example.devin.service.impl;

import com.example.devin.model.ChatRequest;
import com.example.devin.model.ChatResponse;
import com.example.devin.model.Product;
import com.example.devin.service.ChatService;
import com.example.devin.service.ProductService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private OpenAiService openAiService;
    
    @Autowired
    private ProductService productService;
    
    @Value("${llm.api.model}")
    private String llmApiModel;
    
    @Override
    public ChatResponse processChat(ChatRequest request) {
        try {
            // Get product information
            Product product = productService.findById(request.getProductId());
            if (product == null) {
                return new ChatResponse("Product not found", false, "Product not found");
            }
            
            // Create prompt with product context
            String prompt = createPromptWithProductContext(request.getMessage(), product);
            
            // Call LLM API
            String llmResponse = callLlmApi(prompt);
            
            return new ChatResponse(llmResponse, true, null);
        } catch (Exception e) {
            return new ChatResponse("Sorry, I encountered an error processing your request.", false, e.getMessage());
        }
    }
    
    private String createPromptWithProductContext(String userMessage, Product product) {
        return String.format(
            "You are a helpful customer service assistant for a calligraphy art store.\n\n" +
            "Product Information:\n" +
            "- Name: %s\n" +
            "- Price: $%s\n" +
            "- Category: %s\n" +
            "- Description: %s\n" +
            "- Inventory: %d items in stock\n\n" +
            "Customer Question: %s\n\n" +
            "Provide a helpful, concise response about this product. If asked about price, inventory, or features, use the product information provided.",
            product.getProductName(),
            product.getPrice(),
            product.getCategory(),
            product.getDescription(),
            product.getInventory(),
            userMessage
        );
    }
    
    private String callLlmApi(String prompt) {
        try {
            // Create completion request
            CompletionRequest completionRequest = CompletionRequest.builder()
                .model(llmApiModel)
                .prompt(prompt)
                .maxTokens(250)
                .temperature(0.7)
                .topP(0.95)
                .build();
            
            // Call OpenAI API
            CompletionResult completionResult = openAiService.createCompletion(completionRequest);
            
            // Extract response text
            if (completionResult != null && !completionResult.getChoices().isEmpty()) {
                return completionResult.getChoices().get(0).getText().trim();
            }
            
            return "I'm sorry, I couldn't generate a response.";
        } catch (Exception e) {
            // Fallback to simulated responses if API call fails
            return getFallbackResponse(prompt);
        }
    }
    
    private String getFallbackResponse(String prompt) {
        // Simulate response based on prompt content
        if (prompt.toLowerCase().contains("price")) {
            return "The price information is included in the product details above.";
        } else if (prompt.toLowerCase().contains("inventory") || prompt.toLowerCase().contains("stock")) {
            return "We have the inventory information listed in the product details.";
        } else if (prompt.toLowerCase().contains("description") || prompt.toLowerCase().contains("what is")) {
            return "You can find a detailed description in the product information.";
        } else {
            return "I'm here to help with any questions about this calligraphy product. Feel free to ask about price, inventory, features, or anything else!";
        }
    }
}
