package com.urlshortener.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("users")
public class User {

    @PrimaryKey("api_key")
    private String apiKey;

    private UUID id;

    private String tier;

    public User() {
    }

    public User(String apiKey, UUID id, String tier) {
        this.apiKey = apiKey;
        this.id = id;
        this.tier = tier;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }
}
