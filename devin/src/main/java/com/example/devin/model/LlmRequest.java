package com.example.devin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LlmRequest {
    private String prompt;
    private int maxTokens = 250;
    private double temperature = 0.7;
    private double topP = 0.95;
}
