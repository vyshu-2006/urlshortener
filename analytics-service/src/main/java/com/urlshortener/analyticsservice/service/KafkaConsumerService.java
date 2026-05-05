package com.urlshortener.analyticsservice.service;

import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class KafkaConsumerService {

    private final CqlOperations cqlOperations;

    public KafkaConsumerService(CqlOperations cqlOperations) {
        this.cqlOperations = cqlOperations;
    }

    @KafkaListener(topics = "url-clicks", groupId = "analytics-group")
    public void consumeClickEvent(Map<String, Object> event) {
        System.out.println("Consumed event: " + event);

        String shortCode = (String) event.get("shortCode");
        Long timestampMillis = (Long) event.get("timestamp");
        String userAgent = (String) event.get("userAgent");
        String ipAddress = (String) event.get("ipAddress");

        // Parse rudimentary device from User Agent
        String device = "Desktop";
        if (userAgent != null && (userAgent.toLowerCase().contains("mobile") || userAgent.toLowerCase().contains("android") || userAgent.toLowerCase().contains("iphone"))) {
            device = "Mobile";
        }

        // Mock Geo based on IP
        String region = "US"; // In a real scenario, use GeoIP lookup

        // Calculate hour bucket
        Instant instant = Instant.ofEpochMilli(timestampMillis);
        Instant hourBucket = instant.truncatedTo(ChronoUnit.HOURS);

        // Update Cassandra Counters
        // 1. clicks_by_time
        String timeQuery = "UPDATE clicks_by_time SET count = count + 1 WHERE code = ? AND hour_bucket = ?";
        cqlOperations.execute(timeQuery, shortCode, Timestamp.from(hourBucket));

        // 2. clicks_by_device
        String deviceQuery = "UPDATE clicks_by_device SET count = count + 1 WHERE code = ? AND device = ?";
        cqlOperations.execute(deviceQuery, shortCode, device);

        // 3. clicks_by_geo
        String geoQuery = "UPDATE clicks_by_geo SET count = count + 1 WHERE code = ? AND region = ?";
        cqlOperations.execute(geoQuery, shortCode, region);
    }
}
