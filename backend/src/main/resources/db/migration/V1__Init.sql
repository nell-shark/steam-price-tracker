DROP TABLE IF EXISTS apps CASCADE;
DROP TABLE IF EXISTS blocked_apps CASCADE;
DROP TABLE IF EXISTS prices CASCADE;
DROP TABLE IF EXISTS currency_prices CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE apps (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    header_image TEXT NOT NULL,
    is_free BOOLEAN NOT NULL,
    short_description TEXT,
    developers TEXT,
    publishers TEXT,
    website VARCHAR(255),
    metacritic_score INTEGER,
    metacritic_url VARCHAR(255),
    coming_soon BOOLEAN,
    release_date DATE
);

CREATE TABLE platforms (
    app_id BIGINT NOT NULL REFERENCES apps (id),
    platform VARCHAR(32) NOT NULL
);

CREATE TABLE prices (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    app_id BIGINT NOT NULL REFERENCES apps (id),
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE currency_prices (
    price_id BIGINT NOT NULL REFERENCES prices (id),
    currency BPCHAR(3) NOT NULL CHECK (currency IN ('USD', 'EUR', 'RUB', 'KZT')),
    price DOUBLE PRECISION NOT NULL
);

CREATE TABLE blocked_apps (
    id BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password BPCHAR(60) NOT NULL,
    role VARCHAR(255) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN'))
);

CREATE INDEX name_index ON apps (name);
CREATE INDEX email_index ON users (email);
