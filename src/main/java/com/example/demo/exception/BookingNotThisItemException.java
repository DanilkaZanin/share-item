package com.example.demo.exception;

public class BookingNotThisItemException extends RuntimeException {
    public BookingNotThisItemException(String message) {
        super(message);
    }
}
