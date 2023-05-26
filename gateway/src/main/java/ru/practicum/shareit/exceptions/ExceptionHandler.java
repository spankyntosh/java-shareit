package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ErrorResponse> handleException(IllegalArgumentException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }
}
