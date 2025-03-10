package com.example.devin.service.impl;

import com.example.devin.config.LlmApiConfig;
import com.example.devin.model.ChatRequest;
import com.example.devin.model.ChatResponse;
import com.example.devin.model.OpenAIRequest;
import com.example.devin.model.OpenAIResponse;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private LlmApiConfig llmApiConfig;
    
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
            headers.set("Authorization", "Bearer " + llmApiConfig.getApiKey());
            
            // Prepare request body for OpenAI format
            OpenAIRequest request = new OpenAIRequest();
            request.setModel(llmApiConfig.getModel());
            
            List<OpenAIRequest.Message> messages = new ArrayList<>();
            messages.add(new OpenAIRequest.Message("system", "You are a helpful customer service assistant for a calligraphy art store."));
            messages.add(new OpenAIRequest.Message("user", prompt));
            request.setMessages(messages);
            
            // Create HTTP entity
            HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);
            
            // Make API call
            ResponseEntity<OpenAIResponse> response = restTemplate.postForEntity(
                llmApiConfig.getApiUrl(),
                entity,
                OpenAIResponse.class
            );
            
            // Process response
            if (response.getBody() != null && 
                response.getBody().getChoices() != null && 
                !response.getBody().getChoices().isEmpty() &&
                response.getBody().getChoices().get(0).getMessage() != null) {
                
                logger.info("Successfully received response from LLM API");
                return response.getBody().getChoices().get(0).getMessage().getContent().trim();
            }
            
            // Fallback if response parsing fails
            logger.warning("API response was empty or invalid, using fallback response");
            return getFallbackResponse(prompt);
        } catch (RestClientException e) {
            logger.log(Level.WARNING, "Error calling LLM API: " + e.getMessage(), e);
            // Fallback to simulated responses if API call fails
            return getFallbackResponse(prompt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error when calling LLM API: " + e.getMessage(), e);
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
