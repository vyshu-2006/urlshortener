CREATE KEYSPACE IF NOT EXISTS url_shortener 
WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE url_shortener;

CREATE TABLE IF NOT EXISTS users (
    api_key text PRIMARY KEY,
    id uuid,
    tier text
);

CREATE TABLE IF NOT EXISTS urls (
    code text PRIMARY KEY,
    long_url text,
    user_id uuid,
    created_at timestamp
);

CREATE TABLE IF NOT EXISTS clicks_by_time (
    code text,
    hour_bucket timestamp,
    count counter,
    PRIMARY KEY (code, hour_bucket)
) WITH CLUSTERING ORDER BY (hour_bucket DESC);

CREATE TABLE IF NOT EXISTS clicks_by_device (
    code text,
    device text,
    count counter,
    PRIMARY KEY (code, device)
);

CREATE TABLE IF NOT EXISTS clicks_by_geo (
    code text,
    region text,
    count counter,
    PRIMARY KEY (code, region)
);

-- Insert dummy users for testing rate limits
INSERT INTO users (api_key, id, tier) VALUES ('dummy-key-1', 11111111-1111-1111-1111-111111111111, 'free');
INSERT INTO users (api_key, id, tier) VALUES ('dummy-key-2', 22222222-2222-2222-2222-222222222222, 'premium');
INSERT INTO users (api_key, id, tier) VALUES ('dummy-key-3', 33333333-3333-3333-3333-333333333333, 'free');
