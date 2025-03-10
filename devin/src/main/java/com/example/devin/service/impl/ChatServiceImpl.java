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
        // Check if we should use the API or go directly to the fallback
        if (shouldUseLocalResponse()) {
            logger.info("Using local response generation instead of API call");
            return getSmartResponse(prompt);
        }
        
        try {
            logger.info("Attempting to call Hugging Face API");
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
                logger.info("Successfully received response from Hugging Face API");
                return response.getBody().getGenerated_text().trim();
            }
            
            // Fallback if response parsing fails
            logger.warning("API response was empty or invalid, using local response generation");
            return getSmartResponse(prompt);
        } catch (RestClientException e) {
            logger.log(Level.WARNING, "Error calling Hugging Face API: " + e.getMessage(), e);
            // Fallback to simulated responses if API call fails
            return getSmartResponse(prompt);
        }
    }
    
    private boolean shouldUseLocalResponse() {
        // Check if API key is the default one (which won't work)
        if ("hf_demo".equals(huggingFaceConfig.getApiKey())) {
            return true;
        }
        
        // Add other conditions here if needed
        return false;
    }
    
    private String getSmartResponse(String prompt) {
        // Extract product information from the prompt
        Map<String, String> productInfo = extractProductInfo(prompt);
        String userQuestion = extractUserQuestion(prompt);
        
        // Generate a more intelligent response based on the question and product info
        if (userQuestion.toLowerCase().contains("price")) {
            return "This " + productInfo.getOrDefault("name", "calligraphy product") + 
                   " is priced at " + productInfo.getOrDefault("price", "the price shown above") + 
                   ". It's a great value for a product in the " + 
                   productInfo.getOrDefault("category", "calligraphy") + " category.";
        } else if (userQuestion.toLowerCase().contains("inventory") || userQuestion.toLowerCase().contains("stock") || 
                  userQuestion.toLowerCase().contains("available")) {
            return "We currently have " + productInfo.getOrDefault("inventory", "the number shown above") + 
                   " units of this item in stock. If you're interested, I recommend placing your order soon.";
        } else if (userQuestion.toLowerCase().contains("description") || userQuestion.toLowerCase().contains("what is") || 
                  userQuestion.toLowerCase().contains("tell me about")) {
            return "This " + productInfo.getOrDefault("name", "product") + " is " + 
                   productInfo.getOrDefault("description", "a high-quality calligraphy item") + 
                   ". It's perfect for calligraphy enthusiasts looking for quality materials.";
        } else if (userQuestion.toLowerCase().contains("shipping") || userQuestion.toLowerCase().contains("delivery")) {
            return "We offer standard shipping that takes 3-5 business days. Express shipping is also available for an additional fee.";
        } else if (userQuestion.toLowerCase().contains("return") || userQuestion.toLowerCase().contains("refund")) {
            return "We have a 30-day return policy for all our products. If you're not satisfied, you can return it for a full refund.";
        } else {
            return "Thank you for your interest in our " + productInfo.getOrDefault("name", "calligraphy product") + 
                   ". It's one of our popular items in the " + productInfo.getOrDefault("category", "calligraphy") + 
                   " category. If you have any specific questions about its features, price, or availability, feel free to ask!";
        }
    }
    
    private Map<String, String> extractProductInfo(String prompt) {
        Map<String, String> info = new HashMap<>();
        
        // Extract product name
        extractValue(prompt, "Name:", info, "name");
        
        // Extract price
        extractValue(prompt, "Price:", info, "price");
        
        // Extract category
        extractValue(prompt, "Category:", info, "category");
        
        // Extract description
        extractValue(prompt, "Description:", info, "description");
        
        // Extract inventory
        String inventoryStr = extractInventoryValue(prompt, "Inventory:");
        if (inventoryStr != null) {
            info.put("inventory", inventoryStr);
        }
        
        return info;
    }
    
    private void extractValue(String prompt, String prefix, Map<String, String> info, String key) {
        int startIndex = prompt.indexOf(prefix);
        if (startIndex >= 0) {
            startIndex += prefix.length();
            int endIndex = prompt.indexOf("\n", startIndex);
            if (endIndex > startIndex) {
                String value = prompt.substring(startIndex, endIndex).trim();
                info.put(key, value);
            }
        }
    }
    
    private String extractInventoryValue(String prompt, String prefix) {
        int startIndex = prompt.indexOf(prefix);
        if (startIndex >= 0) {
            startIndex += prefix.length();
            int endIndex = prompt.indexOf("items in stock", startIndex);
            if (endIndex > startIndex) {
                return prompt.substring(startIndex, endIndex).trim();
            }
        }
        return null;
    }
    
    private String extractUserQuestion(String prompt) {
        int startIndex = prompt.indexOf("Customer Question:");
        if (startIndex >= 0) {
            startIndex += "Customer Question:".length();
            return prompt.substring(startIndex).trim();
        }
        return prompt;
    }
    
    // Keep the old method for backward compatibility
    private String getFallbackResponse(String prompt) {
        return getSmartResponse(prompt);
    }
}
