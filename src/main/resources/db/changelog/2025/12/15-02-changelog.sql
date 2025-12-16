-- liquibase formatted sql

-- changeset Danila:1765822152688-1
ALTER TABLE users
    ADD roles SMALLINT;

