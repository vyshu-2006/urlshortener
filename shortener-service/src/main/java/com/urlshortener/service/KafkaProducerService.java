package com.urlshortener.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "url-clicks";

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishClickEvent(String shortCode, String userAgent, String ipAddress) {
        Map<String, Object> event = new HashMap<>();
        event.put("shortCode", shortCode);
        event.put("timestamp", System.currentTimeMillis());
        event.put("userAgent", userAgent);
        event.put("ipAddress", ipAddress);
        
        kafkaTemplate.send(TOPIC, shortCode, event);
    }
}
