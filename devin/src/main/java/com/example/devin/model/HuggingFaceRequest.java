package com.example.devin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuggingFaceRequest {
    private String inputs;
    private Parameters parameters;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {
        private Integer max_new_tokens = 250;
        private Double temperature = 0.7;
        private Double top_p = 0.95;
        private Boolean return_full_text = false;
    }
}
