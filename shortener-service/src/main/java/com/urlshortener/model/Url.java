package com.urlshortener.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("urls")
public class Url {

    @PrimaryKey
    private String code;

    @Column("long_url")
    private String longUrl;

    @Column("user_id")
    private UUID userId;

    @Column("created_at")
    private Date createdAt;

    public Url() {
    }

    public Url(String code, String longUrl, UUID userId, Date createdAt) {
        this.code = code;
        this.longUrl = longUrl;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
