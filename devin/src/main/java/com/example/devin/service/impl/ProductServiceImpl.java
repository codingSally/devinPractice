package com.example.devin.service.impl;

import com.example.devin.mapper.ProductMapper;
import com.example.devin.model.Product;
import com.example.devin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Override
    public Product findById(Integer productId) {
        return productMapper.findById(productId);
    }

    @Override
    public List<Product> findByCategory(String category) {
        return productMapper.findByCategory(category);
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productMapper.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<Product> findByPopularity(Integer limit) {
        return productMapper.findByPopularity(limit);
    }

    @Override
    public Product create(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product update(Product product) {
        productMapper.update(product);
        return product;
    }

    @Override
    public void delete(Integer productId) {
        productMapper.delete(productId);
    }
}
