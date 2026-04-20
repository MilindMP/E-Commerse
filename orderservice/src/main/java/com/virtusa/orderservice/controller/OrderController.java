package com.virtusa.orderservice.controller;

import com.virtusa.orderservice.dto.OrderEvent;
import com.virtusa.orderservice.entity.Order;
import com.virtusa.orderservice.exceptions.KafkaPublishException;
import com.virtusa.orderservice.exceptions.ProductNotFoundException;
import com.virtusa.orderservice.repository.OrderRepository;
import com.virtusa.orderservice.service.OrderEventProducer;
import com.virtusa.orderservice.service.ProductClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository repo;
    @Autowired 
    private ProductClient productClient;
    @Autowired 
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private OrderEventProducer orderEventProducer;

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody Order order) throws ProductNotFoundException {
        boolean inStock = productClient.checkStock(order.getProductId());
        if (!inStock)
            throw new ProductNotFoundException("Product is not avaialble in DB");
//            return ResponseEntity.badRequest().body("Product out of stock");

        order.setStatus("PLACED");
        repo.save(order);

        kafkaTemplate.send("order-topic", "Order placed: " + order.getId());
        return ResponseEntity.ok("Order placed successfully");
    }

    @GetMapping("/allorders")
    public List<Order> getAllOrders(){
        return repo.findAll();
    }

    /**
     * Publishes an order event to Kafka with partition awareness
     * @param orderEvent The order event to publish
     * @param partitionKey Optional key to determine the Kafka partition (defaults to customerId)
     * @return Response with the result of the operation
     */
    @PostMapping("/publish-event")
    public ResponseEntity<?> publishOrderEvent(
            @RequestBody OrderEvent orderEvent,
            @RequestParam(required = false) String partitionKey) {
        
        try {
            // Set a unique ID and timestamp if not provided
            if (orderEvent.getOrderId() == null || orderEvent.getOrderId().isEmpty()) {
                orderEvent.setOrderId("order-" + UUID.randomUUID().toString());
            }
            if (orderEvent.getOrderDate() == null) {
                orderEvent.setOrderDate(LocalDateTime.now());
            }
            
            // Send the event to Kafka
            var result = orderEventProducer.sendOrderEvent(orderEvent, partitionKey);
            
            return ResponseEntity.ok().body(
                new ApiResponse<>(
                    true, 
                    "Order event published successfully",
                    new EventPublishResult(
                        orderEvent.getOrderId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                    )
                )
            );
            
        } catch (Exception e) {
            throw new KafkaPublishException("Failed to publish order event: " + e.getMessage(), e);
        }
    }
    
    // Response DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class EventPublishResult {
        private String orderId;
        private String topic;
        private int partition;
        private long offset;
    }
}
