DROP TABLE IF EXISTS apps cascade;
DROP TABLE IF EXISTS blocked_apps cascade;
DROP TABLE IF EXISTS prices cascade;
DROP TABLE IF EXISTS currency_prices cascade;
DROP TABLE IF EXISTS users cascade;

CREATE TABLE apps (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    header_image VARCHAR(255) NOT NULL,
    is_free BOOLEAN NOT NULL,
    short_description TEXT,
    developers TEXT,
    publishers TEXT,
    website VARCHAR(255),
    metacritic_score INT,
    metacritic_url VARCHAR(255),
    coming_soon BOOLEAN,
    release_date DATE
);
CREATE TABLE platforms (
    app_id BIGINT NOT NULL,
    platform VARCHAR(32) NOT NULL,
    FOREIGN KEY (app_id) REFERENCES apps(id)
);
CREATE TABLE prices (
    id BIGSERIAL PRIMARY KEY,
    app_id BIGINT NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (app_id) REFERENCES apps(id)
);
CREATE TABLE currency_prices (
    price_id BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    price BIGINT NOT NULL,
    FOREIGN KEY (price_id) REFERENCES prices(id)
);
CREATE TABLE blocked_apps (
    id BIGINT PRIMARY KEY
);
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE INDEX name_index ON apps (name);
CREATE INDEX email_index ON users (email);
