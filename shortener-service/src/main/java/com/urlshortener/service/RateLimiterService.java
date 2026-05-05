package com.urlshortener.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${rate-limit.capacity:10}")
    private int capacity;

    @Value("${rate-limit.refill-rate:1}")
    private int refillRate; // Tokens per second

    public RateLimiterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String apiKey) {
        String key = "rate_limit:" + apiKey;
        
        // Simple Token Bucket Lua script
        String luaScript = 
            "local key = KEYS[1] " +
            "local capacity = tonumber(ARGV[1]) " +
            "local refillRate = tonumber(ARGV[2]) " +
            "local now = tonumber(ARGV[3]) " +
            "local requested = 1 " +
            
            "local bucket = redis.call('HMGET', key, 'tokens', 'last_refill') " +
            "local tokens = tonumber(bucket[1]) or capacity " +
            "local last_refill = tonumber(bucket[2]) or now " +
            
            "local elapsed = math.max(0, now - last_refill) " +
            "tokens = math.min(capacity, tokens + (elapsed * refillRate)) " +
            
            "if tokens < requested then " +
            "   return 0 " +
            "end " +
            
            "redis.call('HMSET', key, 'tokens', tokens - requested, 'last_refill', now) " +
            "redis.call('EXPIRE', key, math.ceil(capacity / refillRate) + 2) " +
            "return 1";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        long now = System.currentTimeMillis() / 1000;
        List<String> keys = Collections.singletonList(key);

        Long result = redisTemplate.execute(redisScript, keys, capacity, refillRate, now);
        
        return result != null && result == 1;
    }
}
