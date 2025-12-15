package com.example.demo.exception.handler;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.exception.ExistsMailException;
import com.example.demo.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExistsMailException.class)
    public ErrorResponse handleExistingMail(ExistsMailException exception) {
        log.info("Вызван метод handleExistingMail для мэйла: {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound(UserNotFoundException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }
}

