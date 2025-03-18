package com.example.mcp.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Prompt {
    private Long id;
    private String name;
    private String content;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
