package com.example.mcp.service.impl;

import com.example.mcp.mapper.PromptMapper;
import com.example.mcp.model.Prompt;
import com.example.mcp.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class PromptServiceImpl implements PromptService {
    private static final Logger logger = Logger.getLogger(PromptServiceImpl.class.getName());

    private final PromptMapper promptMapper;

    @Autowired
    public PromptServiceImpl(PromptMapper promptMapper) {
        this.promptMapper = promptMapper;
    }

    @Override
    public Prompt getById(Long id) {
        logger.info("Fetching prompt with ID: " + id);
        return promptMapper.findById(id);
    }

    @Override
    public List<Prompt> getAll() {
        logger.info("Fetching all prompts");
        return promptMapper.findAll();
    }

    @Override
    public List<Prompt> getByCategory(String category) {
        logger.info("Fetching prompts by category: " + category);
        return promptMapper.findByCategory(category);
    }

    @Override
    @Transactional
    public Prompt create(Prompt prompt) {
        logger.info("Creating new prompt: " + prompt.getName());
        
        LocalDateTime now = LocalDateTime.now();
        prompt.setCreatedAt(now);
        prompt.setUpdatedAt(now);
        
        promptMapper.insert(prompt);
        return prompt;
    }

    @Override
    @Transactional
    public Prompt update(Prompt prompt) {
        logger.info("Updating prompt: " + prompt.getId());
        
        prompt.setUpdatedAt(LocalDateTime.now());
        
        promptMapper.update(prompt);
        return prompt;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting prompt with ID: " + id);
        return promptMapper.delete(id) > 0;
    }
}
