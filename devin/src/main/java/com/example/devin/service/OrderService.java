package com.example.devin.service;

import com.example.devin.model.Order;
import com.example.devin.model.OrderItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    
    /**
     * Create a new order
     * @param order the order to create
     * @return the created order
     */
    Order createOrder(Order order);
    
    /**
     * Process an order asynchronously
     * @param orderId the ID of the order to process
     * @return CompletableFuture with processing result
     */
    CompletableFuture<String> processOrderAsync(Long orderId);
    
    /**
     * Get an order by ID
     * @param orderId the ID of the order
     * @return the order
     */
    Order getOrderById(Long orderId);
    
    /**
     * Get all orders for a user
     * @param userId the ID of the user
     * @return list of orders
     */
    List<Order> getOrdersByUserId(Integer userId);
    
    /**
     * Update order status
     * @param orderId the ID of the order
     * @param status the new status
     * @return the updated order
     */
    Order updateOrderStatus(Long orderId, String status);
    
    /**
     * Add item to order
     * @param orderId the ID of the order
     * @param item the item to add
     * @return the updated order
     */
    Order addOrderItem(Long orderId, OrderItem item);
    
    /**
     * Remove item from order
     * @param orderId the ID of the order
     * @param orderItemId the ID of the item to remove
     * @return the updated order
     */
    Order removeOrderItem(Long orderId, Long orderItemId);
}
