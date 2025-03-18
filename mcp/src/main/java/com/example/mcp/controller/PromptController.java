package com.example.mcp.controller;

import com.example.mcp.model.Prompt;
import com.example.mcp.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/prompts")
public class PromptController {
    private static final Logger logger = Logger.getLogger(PromptController.class.getName());

    private final PromptService promptService;

    @Autowired
    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prompt> getById(@PathVariable Long id) {
        logger.info("REST request to get prompt by ID: " + id);
        Prompt prompt = promptService.getById(id);
        if (prompt == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prompt);
    }

    @GetMapping
    public ResponseEntity<List<Prompt>> getAll() {
        logger.info("REST request to get all prompts");
        return ResponseEntity.ok(promptService.getAll());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Prompt>> getByCategory(@PathVariable String category) {
        logger.info("REST request to get prompts by category: " + category);
        return ResponseEntity.ok(promptService.getByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Prompt> create(@RequestBody Prompt prompt) {
        logger.info("REST request to create prompt: " + prompt.getName());
        return new ResponseEntity<>(promptService.create(prompt), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prompt> update(@PathVariable Long id, @RequestBody Prompt prompt) {
        logger.info("REST request to update prompt: " + id);
        prompt.setId(id);
        return ResponseEntity.ok(promptService.update(prompt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("REST request to delete prompt with ID: " + id);
        if (promptService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
