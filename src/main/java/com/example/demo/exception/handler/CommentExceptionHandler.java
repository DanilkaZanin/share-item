package com.example.demo.exception.handler;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CommentExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BookingNotThisItemException.class)
    public ErrorResponse handleBookingNotThisItemException(BookingNotFoundException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CommentNotFoudException.class)
    public ErrorResponse handleCommentNotFoudException(CommentNotFoudException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserIsNotBookerException.class)
    public ErrorResponse handleUserIsNotBookerException(UserIsNotBookerException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserIsNotCommentatorException.class)
    public ErrorResponse handleUserIsNotCommentatorException(UserIsNotCommentatorException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }
}
