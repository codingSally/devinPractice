package com.example.devin.service.impl;

import com.example.devin.model.ChatRequest;
import com.example.devin.model.ChatResponse;
import com.example.devin.model.LlmRequest;
import com.example.devin.model.Product;
import com.example.devin.service.ChatService;
import com.example.devin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ProductService productService;
    
    @Value("${llm.api.url}")
    private String llmApiUrl;
    
    @Value("${llm.api.key}")
    private String llmApiKey;
    
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
            // For demo purposes, simulate API call
            // In production, uncomment the API call code
            
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
            
            /* Uncomment for actual API integration
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + llmApiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", llmApiModel);
            requestBody.put("prompt", prompt);
            requestBody.put("max_tokens", 250);
            requestBody.put("temperature", 0.7);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            Map<String, Object> response = restTemplate.postForObject(llmApiUrl, entity, Map.class);
            
            // Extract response text from LLM API response
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    return (String) choices.get(0).get("text");
                }
            }
            
            return "I'm sorry, I couldn't generate a response.";
            */
        } catch (Exception e) {
            return "I'm sorry, I'm having trouble connecting to my knowledge base right now.";
        }
    }
}
