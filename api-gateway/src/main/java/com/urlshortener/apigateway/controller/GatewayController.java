package com.urlshortener.apigateway.controller;

import com.urlshortener.apigateway.router.ConsistentHashRouter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GatewayController {

    private final ConsistentHashRouter router;
    private final RestTemplate restTemplate;

    public GatewayController(ConsistentHashRouter router, RestTemplate restTemplate) {
        this.router = router;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> routeRequest(@PathVariable String shortCode, HttpServletRequest request) {
        String targetNode = router.getNode(shortCode);
        if (targetNode == null) {
            return ResponseEntity.internalServerError().body("No nodes available");
        }

        String targetUrl = targetNode + "/api/v1/urls/" + shortCode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", request.getHeader("User-Agent"));
        headers.set("X-Forwarded-For", request.getRemoteAddr());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Act as API Gateway, forwarding this request to the designated node
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // For 302 redirects, RestTemplate will follow by default unless configured otherwise.
            // Or if it throws for redirect:
            return ResponseEntity.status(500).body("Error routing: " + e.getMessage());
        }
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<?> routeCreateRequest(@RequestBody String body, HttpServletRequest request) {
        // For creations, just use any node or a random node, or the first one. We can just use shortCode="create" to let consistent hashing pick one.
        String targetNode = router.getNode("create");
        String targetUrl = targetNode + "/api/v1/urls";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", request.getHeader("x-api-key"));
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.POST, entity, String.class);
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}
