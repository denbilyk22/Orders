package com.project.orders.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException e) {
        var description = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(field -> field.getField() + " " + field.getDefaultMessage())
                .orElse("Validation error");

        var badRequestStatus = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(badRequestStatus)
                .body(new ExceptionResponse(badRequestStatus.value(), description));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handleException(ApiException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getStatusCode())
                .body(new ExceptionResponse(e.getStatusCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);

        var errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorStatus)
                .body(new ExceptionResponse(errorStatus.value(), e.getMessage()));
    }

}
