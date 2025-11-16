package com.microservice.microservice_task.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFoundException(TaskNotFoundException ex) {
        log.error("Tarea no encontrada: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Task Not Found", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedTaskAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedTaskAccessException(UnauthorizedTaskAccessException ex) {
        log.error("Acceso no autorizado a tarea: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Unauthorized Access", ex.getMessage());
    }

    @ExceptionHandler(InvalidTaskDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTaskDataException(InvalidTaskDataException ex) {
        log.error("Datos de tarea inválidos: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Task Data", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        
        log.error("Error de validación: {}", errors.toString());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errors.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error interno del servidor: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", 
                "Ha ocurrido un error interno del servidor");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        
        return ResponseEntity.status(status).body(errorResponse);
    }
}