package com.example.demo.exception;

public class UserIsNotCommentatorException extends RuntimeException {
    public UserIsNotCommentatorException(String message) {
        super(message);
    }
}
