package com.example.devin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmApiConfig {

    @Value("${llm.api.key}")
    private String apiKey;
    
    @Value("${llm.api.url}")
    private String apiUrl;
    
    @Value("${llm.api.model}")
    private String model;
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public String getModel() {
        return model;
    }
}
