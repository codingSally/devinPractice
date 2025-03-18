package com.example.mcp.service;

import com.example.mcp.model.Prompt;
import java.util.List;

public interface PromptService {
    Prompt getById(Long id);
    List<Prompt> getAll();
    List<Prompt> getByCategory(String category);
    Prompt create(Prompt prompt);
    Prompt update(Prompt prompt);
    boolean delete(Long id);
}
