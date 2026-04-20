package com.virtusa.notificationservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @KafkaListener(topics = "order-topic", groupId = "notification")
    public void listen(String message) {
        System.out.println("Receiving notification from Order service: " + message);

    }
}
