-- liquibase formatted sql

-- changeset Danila:1765825356189-1
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    roles   VARCHAR(255)
);

-- changeset Danila:1765825356189-2
ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset Danila:1765825356189-3
ALTER TABLE users
    DROP COLUMN roles;

