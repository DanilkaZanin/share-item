package com.example.demo.exception.handler;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.exception.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ItemExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public ErrorResponse handleUserNotFound(ItemNotFoundException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }
}
