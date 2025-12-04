package com.example.demo.exception;

public class UserIsNotBookerException extends RuntimeException {
    public UserIsNotBookerException(String message) {
        super(message);
    }
}
