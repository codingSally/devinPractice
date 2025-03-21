package com.example.devin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
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
