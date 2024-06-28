--liquibase formatted sql

--changeset alexander-shlyakhtin:1
CREATE SCHEMA IF NOT EXISTS bybit;
--changeset alexander-shlyakhtin:2
CREATE TABLE IF NOT EXISTS bybit.accounts
(
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL UNIQUE,
    bybit_uid  VARCHAR     NOT NULL UNIQUE,
    username   VARCHAR(16) NOT NULL UNIQUE,
    password   VARCHAR(30) NOT NULL,
    memberType INTEGER     NOT NULL,
    switch     INTEGER     NOT NULL,
    is_Uta     BOOLEAN     NOT NULL,
    is_active  BOOLEAN     NOT NULL
);

