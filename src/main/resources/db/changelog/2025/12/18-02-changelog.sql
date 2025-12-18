-- liquibase formatted sql

-- changeset Danila:1766046171785-1
ALTER TABLE items
    ADD photo_key VARCHAR(255);

-- changeset Danila:1766046171785-5
DROP TABLE item_photos CASCADE;
