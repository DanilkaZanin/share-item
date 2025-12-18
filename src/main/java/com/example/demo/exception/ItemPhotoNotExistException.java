package com.example.demo.exception;

public class ItemPhotoNotExistException extends RuntimeException {
    public ItemPhotoNotExistException(String message) {
        super(message);
    }
}
