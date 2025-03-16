package com.example.devin.controller;

import com.example.devin.model.Order;
import com.example.devin.model.OrderItem;
import com.example.devin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for order management
 * Implements high concurrency patterns with asynchronous processing
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Create a new order
     * @param order the order to create
     * @return the created order
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }
    
    /**
     * Get an order by ID
     * @param orderId the ID of the order
     * @return the order
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all orders for the current user
     * @param userId the ID of the user
     * @return list of orders
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    
    /**
     * Update order status
     * @param orderId the ID of the order
     * @param status the new status
     * @return the updated order
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Add item to order
     * @param orderId the ID of the order
     * @param item the item to add
     * @return the updated order
     */
    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Order> addOrderItem(
            @PathVariable Long orderId,
            @RequestBody OrderItem item) {
        Order order = orderService.addOrderItem(orderId, item);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Remove item from order
     * @param orderId the ID of the order
     * @param orderItemId the ID of the item to remove
     * @return the updated order
     */
    @DeleteMapping("/{orderId}/items/{orderItemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Order> removeOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId) {
        Order order = orderService.removeOrderItem(orderId, orderItemId);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Process an order asynchronously
     * @param orderId the ID of the order to process
     * @return processing result
     */
    @PostMapping("/{orderId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<String>> processOrder(@PathVariable Long orderId) {
        return orderService.processOrderAsync(orderId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body(ex.getMessage()));
    }
}
