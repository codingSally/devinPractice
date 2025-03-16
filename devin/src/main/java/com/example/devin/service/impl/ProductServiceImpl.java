package com.example.devin.service.impl;

import com.example.devin.mapper.ProductMapper;
import com.example.devin.model.Product;
import com.example.devin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Cacheable(value = "products", key = "'all'")
    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Cacheable(value = "products", key = "#productId")
    @Override
    public Product findById(Integer productId) {
        return productMapper.findById(productId);
    }

    @Cacheable(value = "productsByCategory", key = "#category")
    @Override
    public List<Product> findByCategory(String category) {
        return productMapper.findByCategory(category);
    }

    @Cacheable(value = "productsByPriceRange", key = "#minPrice + '-' + #maxPrice")
    @Override
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productMapper.findByPriceRange(minPrice, maxPrice);
    }

    @Cacheable(value = "popularProducts", key = "#limit")
    @Override
    public List<Product> findByPopularity(Integer limit) {
        return productMapper.findByPopularity(limit);
    }

    @Caching(evict = {
        @CacheEvict(value = "products", key = "'all'"),
        @CacheEvict(value = "productsByCategory", allEntries = true),
        @CacheEvict(value = "productsByPriceRange", allEntries = true),
        @CacheEvict(value = "popularProducts", allEntries = true)
    })
    @Override
    public Product create(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Caching(
        put = {
            @CachePut(value = "products", key = "#product.productId")
        },
        evict = {
            @CacheEvict(value = "products", key = "'all'"),
            @CacheEvict(value = "productsByCategory", allEntries = true),
            @CacheEvict(value = "productsByPriceRange", allEntries = true),
            @CacheEvict(value = "popularProducts", allEntries = true)
        }
    )
    @Override
    public Product update(Product product) {
        productMapper.update(product);
        return product;
    }

    @Caching(evict = {
        @CacheEvict(value = "products", key = "#productId"),
        @CacheEvict(value = "products", key = "'all'"),
        @CacheEvict(value = "productsByCategory", allEntries = true),
        @CacheEvict(value = "productsByPriceRange", allEntries = true),
        @CacheEvict(value = "popularProducts", allEntries = true)
    })
    @Override
    public void delete(Integer productId) {
        productMapper.delete(productId);
    }
}
