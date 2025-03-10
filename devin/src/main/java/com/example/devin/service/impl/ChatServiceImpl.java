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
        // Try multiple API endpoints if configured ones fail
        String[] apiEndpoints = getApiEndpointsToTry();
        String originalApiUrl = llmApiConfig.getApiUrl();
        String originalModel = llmApiConfig.getModel();
        
        for (String endpoint : apiEndpoints) {
            try {
                // Parse endpoint string (format: "url|model")
                String[] parts = endpoint.split("\\|");
                String apiUrl = parts[0];
                String model = parts.length > 1 ? parts[1] : originalModel;
                
                logger.info("Attempting LLM API call to: " + apiUrl + " with model: " + model);
                
                // Prepare headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                // Only add Authorization header if API key is provided
                if (llmApiConfig.getApiKey() != null && !llmApiConfig.getApiKey().isEmpty()) {
                    headers.set("Authorization", "Bearer " + llmApiConfig.getApiKey());
                }
                
                // Prepare request body for OpenAI format
                OpenAIRequest request = new OpenAIRequest();
                request.setModel(model);
                
                List<OpenAIRequest.Message> messages = new ArrayList<>();
                messages.add(new OpenAIRequest.Message("system", "You are a helpful customer service assistant for a calligraphy art store."));
                messages.add(new OpenAIRequest.Message("user", prompt));
                request.setMessages(messages);
                
                // Create HTTP entity
                HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);
                
                // Set timeout for this request (3 seconds to fail fast)
                int originalTimeout = ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).getReadTimeout();
                ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setReadTimeout(3000);
                
                // Make API call
                ResponseEntity<OpenAIResponse> response = restTemplate.postForEntity(
                    apiUrl,
                    entity,
                    OpenAIResponse.class
                );
                
                // Reset timeout
                ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setReadTimeout(originalTimeout);
                
                // Process response
                if (response.getBody() != null && 
                    response.getBody().getChoices() != null && 
                    !response.getBody().getChoices().isEmpty() &&
                    response.getBody().getChoices().get(0).getMessage() != null) {
                    
                    logger.info("Successfully received response from LLM API: " + apiUrl);
                    return response.getBody().getChoices().get(0).getMessage().getContent().trim();
                }
                
                logger.warning("API response from " + apiUrl + " was empty or invalid, trying next endpoint");
            } catch (RestClientException e) {
                logger.log(Level.WARNING, "Error calling LLM API at " + endpoint + ": " + e.getMessage());
                
                // Specific error handling for different endpoints
                if (endpoint.contains("localhost") && e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                    logger.warning("Connection refused to local Ollama server. Make sure Ollama is running with 'ollama serve'");
                } else if (endpoint.contains("chatanywhere") && e.getMessage() != null) {
                    logger.warning("ChatAnywhere API error: " + e.getMessage());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Unexpected error when calling LLM API at " + endpoint + ": " + e.getMessage());
            }
        }
        
        // All API endpoints failed, use fallback
        logger.warning("All LLM API endpoints failed, using intelligent fallback response");
        return getFallbackResponse(prompt);
    }
    
    private String[] getApiEndpointsToTry() {
        // Try the configured endpoint first, then fallbacks
        String configuredEndpoint = llmApiConfig.getApiUrl() + "|" + llmApiConfig.getModel();
        
        return new String[] {
            configuredEndpoint,
            // Try Ollama if not already configured
            configuredEndpoint.contains("localhost") ? null : "http://localhost:11434/v1/chat/completions|llama2",
            // Try ChatAnywhere if not already configured
            configuredEndpoint.contains("chatanywhere") ? null : "https://api.chatanywhere.com.cn/v1/chat/completions|gpt-3.5-turbo"
        };
    }
    
    private String getFallbackResponse(String prompt) {
        // Extract product information from the prompt
        String productName = extractFromPrompt(prompt, "Name:", "Price:");
        String price = extractFromPrompt(prompt, "Price: $", "Category:");
        String category = extractFromPrompt(prompt, "Category:", "Description:");
        String description = extractFromPrompt(prompt, "Description:", "Inventory:");
        String inventory = extractFromPrompt(prompt, "Inventory:", "items in stock");
        String question = extractFromPrompt(prompt, "Customer Question:", "Provide a helpful");
        
        // Generate a more intelligent fallback response based on the question
        if (question.toLowerCase().contains("price") || question.toLowerCase().contains("cost") || question.toLowerCase().contains("how much")) {
            return "The " + productName + " costs $" + price + ". It's a great value for a quality calligraphy product in the " + category + " category.";
        } else if (question.toLowerCase().contains("inventory") || question.toLowerCase().contains("stock") || question.toLowerCase().contains("available")) {
            return "We currently have " + inventory + " units of the " + productName + " in stock and ready to ship.";
        } else if (question.toLowerCase().contains("description") || question.toLowerCase().contains("what is") || question.toLowerCase().contains("tell me about")) {
            return "The " + productName + " is " + description + " It's one of our popular items in the " + category + " category.";
        } else if (question.toLowerCase().contains("category") || question.toLowerCase().contains("type")) {
            return "The " + productName + " belongs to our " + category + " category of calligraphy products.";
        } else {
            return "Thank you for your interest in the " + productName + ". It costs $" + price + " and we have " + inventory + 
                   " in stock. It's a " + category + " product. " + description + 
                   " Please let me know if you have any other questions!";
        }
    }
    
    private String extractFromPrompt(String prompt, String startMarker, String endMarker) {
        try {
            int startIndex = prompt.indexOf(startMarker);
            if (startIndex == -1) return "";
            
            startIndex += startMarker.length();
            int endIndex = prompt.indexOf(endMarker, startIndex);
            
            if (endIndex == -1) {
                return prompt.substring(startIndex).trim();
            }
            
            return prompt.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            logger.warning("Error extracting information from prompt: " + e.getMessage());
            return "";
        }
    }
}
