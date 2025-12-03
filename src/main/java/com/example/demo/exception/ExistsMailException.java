package com.example.demo.exception;

public class ExistsMailException extends RuntimeException {
    public ExistsMailException(String message) {
        super(message);
    }
}
