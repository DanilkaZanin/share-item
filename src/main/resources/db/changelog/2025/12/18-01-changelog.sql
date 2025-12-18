-- liquibase formatted sql

-- changeset Danila:1766037625388-2
ALTER TABLE user_photos
    DROP CONSTRAINT fk_user_photos_on_user;

-- changeset Danila:1766037625388-1
ALTER TABLE users
    ADD photo_key VARCHAR(255);

-- changeset Danila:1766037625388-3
DROP TABLE user_photos CASCADE;

