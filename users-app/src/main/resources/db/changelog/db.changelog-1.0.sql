--liquibase formatted sql

--changeset shlyakhtin:alexander:1
CREATE TABLE IF NOT EXISTS users
(
    user_id                    uuid         not null
        constraint pk_users primary key,
    phone                      varchar(100) not null unique,
    email                      varchar(255) unique,
    password                   varchar(255) not null,
    first_name                 varchar(255),
    last_name                  varchar(255),
    is_enabled                 boolean      not null,
    is_user_data_non_pending   boolean      not null,
    is_account_non_locked      boolean      not null,
    is_credentials_non_expired boolean      not null,
    is_registration_complete   boolean      not null,
    role                       varchar(255)
);
--changeset shlyakhtin:alexander:2
CREATE INDEX idx_users_phone ON users (phone);

--changeset shlyakhtin:alexander:3
CREATE TABLE IF NOT EXISTS otp_token_login
(
    id              varchar(255) not null
        constraint pk_otp_token primary key,
    token           varchar(6),
    expiration_time timestamp with time zone,
    valid           boolean
);

--changeset shlyakhtin:alexander:4
CREATE TABLE IF NOT EXISTS otp_token_registration
(
    id              varchar(255) primary key not null,
    token           varchar(6),
    expiration_time timestamp with time zone,
    valid           boolean
);




