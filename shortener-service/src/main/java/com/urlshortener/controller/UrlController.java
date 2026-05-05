package com.urlshortener.controller;

import com.urlshortener.service.RateLimiterService;
import com.urlshortener.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlShortenerService urlShortenerService;
    private final RateLimiterService rateLimiterService;

    public UrlController(UrlShortenerService urlShortenerService, RateLimiterService rateLimiterService) {
        this.urlShortenerService = urlShortenerService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping
    public ResponseEntity<?> shortenUrl(@RequestHeader("x-api-key") String apiKey,
                                        @RequestBody Map<String, String> payload) {
        if (!rateLimiterService.allowRequest(apiKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded.");
        }

        String longUrl = payload.get("longUrl");
        if (longUrl == null || longUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("longUrl is required");
        }

        try {
            String shortCode = urlShortenerService.shortenUrl(longUrl, apiKey);
            return ResponseEntity.ok(Map.of("shortCode", shortCode, "shortUrl", "http://localhost:8080/" + shortCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode,
                                      @RequestHeader(value = "User-Agent", defaultValue = "Unknown") String userAgent,
                                      @RequestHeader(value = "X-Forwarded-For", defaultValue = "127.0.0.1") String ipAddress) {
        try {
            String longUrl = urlShortenerService.getLongUrl(shortCode, userAgent, ipAddress);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(longUrl))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
