package com.example.devin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer inventory;
    private String imagePath;
    private String category;
    private Integer popularity;
    private String createdAt;
    private String updatedAt;
}
