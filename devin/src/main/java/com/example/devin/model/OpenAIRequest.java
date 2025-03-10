package com.example.devin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequest {
    private String model;
    private List<Message> messages;
    private Double temperature = 0.7;
    private Integer max_tokens = 800;
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    @Data
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
