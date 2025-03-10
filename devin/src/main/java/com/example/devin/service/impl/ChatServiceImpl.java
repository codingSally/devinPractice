package com.example.devin.service.impl;

import com.example.devin.config.HuggingFaceConfig;
import com.example.devin.model.ChatRequest;
import com.example.devin.model.ChatResponse;
import com.example.devin.model.HuggingFaceRequest;
import com.example.devin.model.HuggingFaceResponse;
import com.example.devin.model.Product;
import com.example.devin.service.ChatService;
import com.example.devin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = Logger.getLogger(ChatServiceImpl.class.getName());
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private HuggingFaceConfig huggingFaceConfig;
    
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
            logger.log(Level.SEVERE, "Error processing chat request", e);
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
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + huggingFaceConfig.getApiKey());
            
            // Prepare request body - simplify to just include inputs
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("inputs", prompt);
            
            // Create HTTP entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);
            
            // Make API call
            ResponseEntity<HuggingFaceResponse> response = restTemplate.postForEntity(
                huggingFaceConfig.getApiUrl(),
                entity,
                HuggingFaceResponse.class
            );
            
            // Process response
            if (response.getBody() != null && response.getBody().getGenerated_text() != null) {
                return response.getBody().getGenerated_text().trim();
            }
            
            // Fallback if response parsing fails
            return getFallbackResponse(prompt);
        } catch (RestClientException e) {
            logger.log(Level.WARNING, "Error calling Hugging Face API", e);
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
