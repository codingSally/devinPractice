package com.example.devin.service;

import com.example.devin.model.ChatRequest;
import com.example.devin.model.ChatResponse;

public interface ChatService {
    ChatResponse processChat(ChatRequest request);
}
