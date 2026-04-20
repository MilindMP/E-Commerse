package com.virtusa.orderservice.service;

import com.virtusa.orderservice.dto.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class OrderEventProducer {

    @Value("${spring.kafka.topic.order-events}")
    private String orderEventsTopic;

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends an order event to Kafka with a specific partition key
     * 
     * @param orderEvent   The order event to send
     * @param partitionKey The key used to determine the partition (e.g.,
     *                     customerId, orderId)
     * @return The result of the send operation
     */
    public SendResult<String, OrderEvent> sendOrderEvent(OrderEvent orderEvent, String partitionKey)
            throws InterruptedException, ExecutionException, TimeoutException {

        // If no partition key is provided, use a default one
        String key = (partitionKey != null && !partitionKey.isEmpty()) ? partitionKey : orderEvent.getCustomerId();

        // Send the message and wait for confirmation
        return kafkaTemplate.send(orderEventsTopic, key, orderEvent)
                .get(10, TimeUnit.SECONDS);
    }

    /**
     * Sends an order event to Kafka asynchronously with a callback
     * 
     * @param orderEvent   The order event to send
     * @param partitionKey The key used to determine the partition
     * @param callback     The callback to handle the result or failure
     */
    public void sendOrderEventAsync(OrderEvent orderEvent, String partitionKey,
            ListenableFutureCallback<SendResult<String, OrderEvent>> callback) {

        String key = (partitionKey != null && !partitionKey.isEmpty()) ? partitionKey : orderEvent.getCustomerId();

        ListenableFuture<SendResult<String, OrderEvent>> future = (ListenableFuture<SendResult<String, OrderEvent>>) kafkaTemplate
                .send(orderEventsTopic, key, orderEvent);

        future.addCallback(callback);
    }

    /**
     * Custom partition strategy based on order event properties
     * This ensures that related messages go to the same partition
     */
    private String partitionEvent(OrderEvent orderEvent, String partitionKey) {
        // Simple hash-based partitioning using the provided key
        // This ensures the same key always goes to the same partition
        return String.valueOf(Math.abs(partitionKey.hashCode() % 3));
    }
}
