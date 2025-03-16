package com.example.devin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long orderItemId;
    private Long orderId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
