package com.example.devin.mapper;

import com.example.devin.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> findAll();
    Product findById(@Param("productId") Integer productId);
    List<Product> findByCategory(@Param("category") String category);
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    List<Product> findByPopularity(@Param("limit") Integer limit);
    int insert(Product product);
    int update(Product product);
    int delete(@Param("productId") Integer productId);
}
