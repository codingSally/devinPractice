package com.example.devin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuggingFaceResponse {
    private String generated_text;
    
    // Keep the inner class for backward compatibility
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedText {
        private String text;
    }
}
