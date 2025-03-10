package com.example.devin.controller;

import com.example.devin.model.ChatRequest;
import com.example.devin.model.ChatResponse;
import com.example.devin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    @PostMapping("/product/{productId}")
    public ResponseEntity<ChatResponse> processChat(
        @PathVariable Integer productId,
        @RequestBody ChatRequest request
    ) {
        // Set the productId from the path variable
        request.setProductId(productId);
        
        // Process the chat request
        ChatResponse response = chatService.processChat(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
