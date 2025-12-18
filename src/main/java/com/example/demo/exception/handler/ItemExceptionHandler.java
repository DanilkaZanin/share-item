package com.example.demo.exception.handler;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.exception.ItemPhotoExistException;
import com.example.demo.exception.ItemPhotoNotExistException;
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
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ItemPhotoExistException.class)
    public ErrorResponse handleItemPhotoExistException(ItemPhotoExistException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ItemPhotoNotExistException.class)
    public ErrorResponse handleItemPhotoNotExistException(ItemPhotoNotExistException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now()
        );
    }
}
