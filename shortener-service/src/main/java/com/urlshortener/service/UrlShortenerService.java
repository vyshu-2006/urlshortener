package com.urlshortener.service;

import com.urlshortener.model.Url;
import com.urlshortener.model.User;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.repository.UserRepository;
import com.urlshortener.util.Base62Encoder;
import com.urlshortener.util.SnowflakeIdGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UrlShortenerService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final Base62Encoder base62Encoder;
    private final SnowflakeIdGenerator idGenerator;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaProducerService kafkaProducerService;

    public UrlShortenerService(UrlRepository urlRepository,
                               UserRepository userRepository,
                               Base62Encoder base62Encoder,
                               SnowflakeIdGenerator idGenerator,
                               RedisTemplate<String, Object> redisTemplate,
                               KafkaProducerService kafkaProducerService) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
        this.base62Encoder = base62Encoder;
        this.idGenerator = idGenerator;
        this.redisTemplate = redisTemplate;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String shortenUrl(String longUrl, String apiKey) {
        // Validate user
        Optional<User> userOpt = userRepository.findById(apiKey);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid API Key");
        }

        // Generate ID and encode
        long id = idGenerator.nextId();
        String shortCode = base62Encoder.encode(id);

        // Save to DB
        Url url = new Url(shortCode, longUrl, userOpt.get().getId(), new Date());
        urlRepository.save(url);

        // Cache in Redis (pre-warm)
        redisTemplate.opsForValue().set("url:" + shortCode, longUrl, 7, TimeUnit.DAYS);

        return shortCode;
    }

    public String getLongUrl(String shortCode, String userAgent, String ipAddress) {
        // Try Redis cache first
        String cacheKey = "url:" + shortCode;
        String longUrl = (String) redisTemplate.opsForValue().get(cacheKey);

        if (longUrl == null) {
            // Cache Miss -> Check DB
            Optional<Url> urlOpt = urlRepository.findById(shortCode);
            if (urlOpt.isPresent()) {
                longUrl = urlOpt.get().getLongUrl();
                // Update Cache
                redisTemplate.opsForValue().set(cacheKey, longUrl, 7, TimeUnit.DAYS);
            } else {
                throw new IllegalArgumentException("URL not found");
            }
        }

        // Asynchronously publish to Kafka
        kafkaProducerService.publishClickEvent(shortCode, userAgent, ipAddress);

        return longUrl;
    }
}
