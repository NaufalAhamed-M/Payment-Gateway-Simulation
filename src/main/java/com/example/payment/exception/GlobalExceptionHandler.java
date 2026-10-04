package com.example.payment.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(),error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Request validation failed");
        response.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleIdempotencyConflict(IdempotencyConflictException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 409);
        response.put("error", "IDEMPOTENCY_CONFLICT");
        response.put("message", ex.getMessage());

        return response;
    }

    @ExceptionHandler(MaxRetryExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleMaxRetryExceeded(MaxRetryExceededException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 409);
        response.put("error", "MAX_RETRY_EXCEEDED");
        response.put("message", ex.getMessage());

        return response;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "BAD_REQUEST");
        response.put("message", ex.getMessage());

        return response;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 409);
        response.put("error", "CONFLICT");
        response.put("message", "Duplicate or conflicting database operation");
        return response;
    }
}
