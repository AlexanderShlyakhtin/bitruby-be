--liquibase formatted sql

--changeset alexander-shlyakhtin:1
CREATE TABLE IF NOT EXISTS bybit.accounts
(
    id         UUID PRIMARY KEY,
    version    BIGINT NOT NULL,
    user_id    UUID NOT NULL UNIQUE,
    bybit_uid  VARCHAR     UNIQUE,
    username   VARCHAR(16) NOT NULL UNIQUE,
    password   VARCHAR(30) NOT NULL,
    member_type INTEGER     NOT NULL,
    switch     INTEGER     NOT NULL,
    is_Uta     BOOLEAN     NOT NULL,
    is_active  BOOLEAN     NOT NULL
);

