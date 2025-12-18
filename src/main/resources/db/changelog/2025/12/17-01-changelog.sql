-- liquibase formatted sql

-- changeset Danila:1765983721755-1
CREATE TABLE item_photos
(
    item_id    BIGINT NOT NULL,
    photo_keys VARCHAR(255)
);

-- changeset Danila:1765983721755-2
CREATE TABLE user_photos
(
    user_id    BIGINT NOT NULL,
    photo_keys VARCHAR(255)
);

-- changeset Danila:1765983721755-3
ALTER TABLE item_photos
    ADD CONSTRAINT fk_item_photos_on_item FOREIGN KEY (item_id) REFERENCES items (id);

-- changeset Danila:1765983721755-4
ALTER TABLE user_photos
    ADD CONSTRAINT fk_user_photos_on_user FOREIGN KEY (user_id) REFERENCES users (id);

