package com.example.demo.exception.handler;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.BookingNotFoundException;
import com.example.demo.exception.DateConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class BookingExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookingNotFoundException.class)
    public ErrorResponse handleBookingNotFound(BookingNotFoundException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateConflictException.class)
    public ErrorResponse handleDateConflict(DateConflictException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleUserNotFound(AccessDeniedException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }
}
