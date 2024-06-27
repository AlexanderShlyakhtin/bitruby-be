--liquibase formatted sql

--changeset alexander-shlyakhtin:1
CREATE TABLE IF NOT EXISTS users
(
    user_id                    uuid         not null
        constraint pk_users primary key,
    phone                      varchar(100) not null unique,
    email                      varchar(255) not null unique,
    password                   varchar(255) not null,
    first_name                 varchar(255),
    last_name                  varchar(255),
    is_enabled                 boolean      not null,
    is_verified                boolean      not null,
    is_bybit_account_created   boolean      not null,
    is_account_non_locked      boolean      not null,
    is_credentials_non_expired boolean      not null,
    is_registration_complete   boolean      not null,
    address                    varchar,
    role                       varchar(255)
);
--changeset alexander-shlyakhtin:2
CREATE INDEX idx_users_phone ON users (phone);

--changeset alexander-shlyakhtin:3
CREATE TABLE IF NOT EXISTS otp_token_login
(
    id              varchar(255) not null
        constraint pk_otp_token primary key,
    token           varchar(6),
    expiration_time timestamp with time zone,
    valid           boolean
);

--changeset alexander-shlyakhtin:4
CREATE TABLE IF NOT EXISTS otp_token_registration
(
    id              varchar(255) primary key not null,
    token           varchar(6),
    expiration_time timestamp with time zone,
    valid           boolean
);

--changeset alexander-shlyakhtin:5
CREATE TABLE IF NOT EXISTS otp_token_restore_password
(
    id              varchar(255) primary key not null,
    token           varchar(6),
    expiration_time timestamp with time zone,
    valid           boolean
);
--changeset alexander-shlyakhtin:6
CREATE TABLE IF NOT EXISTS users_verification_sessions
(
    id          UUID PRIMARY KEY,
    user_id     UUID        not null,
    session_url VARCHAR     not null,
    status      VARCHAR     not null,
    active      BOOLEAN     not null,
    created     timestamptz not NULL,
    updated     timestamptz not NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    UNIQUE (id, user_id)
);
--changeset alexander-shlyakhtin:7
CREATE TABLE IF NOT EXISTS users_documents
(
    id            UUID PRIMARY KEY,
    user_id       UUID         not null,
    payload       bytea        not null,
    document_type varchar(255) not null,

    FOREIGN KEY (user_id) REFERENCES users (user_id)
);




