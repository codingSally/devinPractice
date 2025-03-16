package com.example.devin.service.impl;

import com.example.devin.model.Order;
import com.example.devin.model.OrderItem;
import com.example.devin.service.AsyncTaskService;
import com.example.devin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Implementation of OrderService
 * Uses asynchronous processing for high concurrency
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    private static final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
    
    // In-memory store for orders (would be a database in production)
    private final Map<Long, Order> orderStore = new ConcurrentHashMap<>();
    private long orderIdCounter = 1;
    private long orderItemIdCounter = 1;
    
    @Autowired
    private AsyncTaskService asyncTaskService;
    
    @Override
    public Order createOrder(Order order) {
        // Set order ID if not provided
        if (order.getOrderId() == null) {
            order.setOrderId(orderIdCounter++);
        }
        
        // Set order item IDs if not provided
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getOrderItemId() == null) {
                    item.setOrderItemId(orderItemIdCounter++);
                }
                item.setOrderId(order.getOrderId());
            }
        } else {
            order.setItems(new ArrayList<>());
        }
        
        // Set initial status
        if (order.getStatus() == null) {
            order.setStatus(Order.STATUS_PENDING);
        }
        
        // Store the order
        orderStore.put(order.getOrderId(), order);
        logger.info("Created order: " + order.getOrderId());
        
        // Process the order asynchronously
        processOrderAsync(order.getOrderId());
        
        return order;
    }
    
    @Override
    public CompletableFuture<String> processOrderAsync(Long orderId) {
        logger.info("Starting async processing for order: " + orderId);
        
        // Get the order
        Order order = getOrderById(orderId);
        if (order == null) {
            return CompletableFuture.failedFuture(new RuntimeException("Order not found: " + orderId));
        }
        
        // Update order status
        order.setStatus(Order.STATUS_PROCESSING);
        orderStore.put(orderId, order);
        
        // Process the order asynchronously
        CompletableFuture<String> orderProcessing = asyncTaskService.processOrderAsync(orderId);
        
        // Update inventory for each item asynchronously
        List<CompletableFuture<String>> inventoryUpdates = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            inventoryUpdates.add(asyncTaskService.updateInventoryAsync(item.getProductId(), item.getQuantity()));
        }
        
        // Send notification to user asynchronously
        CompletableFuture<String> notification = asyncTaskService.sendNotificationAsync(
                order.getUserId().longValue(), "Your order is being processed");
        
        // Combine all futures
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                orderProcessing,
                CompletableFuture.allOf(inventoryUpdates.toArray(new CompletableFuture[0])),
                notification
        );
        
        // When all tasks complete, update order status
        return allTasks.thenApply(v -> {
            order.setStatus(Order.STATUS_COMPLETED);
            orderStore.put(orderId, order);
            return "Order " + orderId + " processed successfully";
        }).exceptionally(ex -> {
            logger.severe("Error processing order " + orderId + ": " + ex.getMessage());
            order.setStatus(Order.STATUS_CANCELLED);
            orderStore.put(orderId, order);
            return "Order " + orderId + " processing failed: " + ex.getMessage();
        });
    }
    
    @Override
    public Order getOrderById(Long orderId) {
        return orderStore.get(orderId);
    }
    
    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if (order.getUserId().equals(userId)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }
    
    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(status);
            orderStore.put(orderId, order);
        }
        return order;
    }
    
    @Override
    public Order addOrderItem(Long orderId, OrderItem item) {
        Order order = getOrderById(orderId);
        if (order != null) {
            item.setOrderItemId(orderItemIdCounter++);
            item.setOrderId(orderId);
            order.getItems().add(item);
            orderStore.put(orderId, order);
        }
        return order;
    }
    
    @Override
    public Order removeOrderItem(Long orderId, Long orderItemId) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.getItems().removeIf(item -> item.getOrderItemId().equals(orderItemId));
            orderStore.put(orderId, order);
        }
        return order;
    }
}
