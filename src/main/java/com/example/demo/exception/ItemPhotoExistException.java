package com.example.demo.exception;

public class ItemPhotoExistException extends RuntimeException {
    public ItemPhotoExistException(String message) {
        super(message);
    }
}
