package com.example.demo.exceptions;

public class ExistsMailException extends RuntimeException {
    public ExistsMailException(String message) {
        super(message);
    }
}
