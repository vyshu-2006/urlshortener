package com.urlshortener.analyticsservice.controller;

import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*") // Allows the React Dashboard to call this
public class AnalyticsController {

    private final CqlOperations cqlOperations;

    public AnalyticsController(CqlOperations cqlOperations) {
        this.cqlOperations = cqlOperations;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> getAnalytics(@PathVariable String shortCode) {
        
        // 1. Get clicks by time
        List<Map<String, Object>> timeRows = cqlOperations.queryForList("SELECT hour_bucket, count FROM clicks_by_time WHERE code = ?", shortCode);
        List<Map<String, Object>> timeData = timeRows.stream().map(row -> Map.of(
                "hour", row.get("hour_bucket").toString(),
                "clicks", row.get("count")
        )).collect(Collectors.toList());

        // 2. Get clicks by device
        List<Map<String, Object>> deviceRows = cqlOperations.queryForList("SELECT device, count FROM clicks_by_device WHERE code = ?", shortCode);
        List<Map<String, Object>> deviceData = deviceRows.stream().map(row -> Map.of(
                "device", row.get("device").toString(),
                "clicks", row.get("count")
        )).collect(Collectors.toList());

        // 3. Get clicks by geo
        List<Map<String, Object>> geoRows = cqlOperations.queryForList("SELECT region, count FROM clicks_by_geo WHERE code = ?", shortCode);
        List<Map<String, Object>> geoData = geoRows.stream().map(row -> Map.of(
                "region", row.get("region").toString(),
                "clicks", row.get("count")
        )).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "timeSeries", timeData,
                "deviceStats", deviceData,
                "geoStats", geoData
        ));
    }
}
