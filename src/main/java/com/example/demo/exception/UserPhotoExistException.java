package com.example.demo.exception;

public class UserPhotoExistException extends RuntimeException {
    public UserPhotoExistException(String message) {
        super(message);
    }
}
