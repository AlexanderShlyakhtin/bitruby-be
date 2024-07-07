--liquibase formatted sql

--changeset alexander-shlyakhtin:1
CREATE TABLE IF NOT EXISTS users.users
(
    user_id            uuid         not null PRIMARY KEY,
    version            BIGINT       not null,
    phone              varchar(100) not null,
    email              varchar(255) not null,
    password           varchar(255) not null,
    first_name         varchar(255),
    last_name          varchar(255),
    country            varchar(255),
    address            varchar(1000),
    is_enabled         boolean      not null,
    is_email_confirmed boolean      not null,
    is_phone_confirmed boolean      not null,
    account_status     varchar(30)  not null,
    role               varchar(255)
);

--changeset alexander-shlyakhtin:2
CREATE INDEX idx_users_phone ON users.users (phone);
CREATE INDEX idx_users_email ON users.users (email);

--changeset alexander-shlyakhtin:3
CREATE TABLE IF NOT EXISTS users.users_verification_sessions
(
    id          UUID PRIMARY KEY,
    user_id     UUID        not null,
    session_url VARCHAR     not null,
    status      VARCHAR     not null,
    active      BOOLEAN     not null,
    created     timestamptz not NULL,
    updated     timestamptz not NULL,
    FOREIGN KEY (user_id) REFERENCES users.users (user_id),
    UNIQUE (id, user_id)
);
--changeset alexander-shlyakhtin:4
CREATE TABLE IF NOT EXISTS users.users_documents
(
    id            UUID PRIMARY KEY,
    user_id       UUID         not null,
    payload       bytea        not null,
    document_type varchar(255) not null,

    FOREIGN KEY (user_id) REFERENCES users.users (user_id)
);
