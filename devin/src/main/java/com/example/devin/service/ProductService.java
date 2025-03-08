package com.example.devin.service;

import com.example.devin.model.Product;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Integer productId);
    List<Product> findByCategory(String category);
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByPopularity(Integer limit);
    Product create(Product product);
    Product update(Product product);
    void delete(Integer productId);
}
