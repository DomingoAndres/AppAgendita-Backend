package com.microservice.microservice_user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el microservicio de usuarios
 * Proporciona respuestas HTTP consistentes en formato JSON estándar
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones cuando un usuario no es encontrado
     * @return 404 NOT_FOUND
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("Usuario no encontrado: {}", ex.getMessage());
        return buildErrorResponse(
            HttpStatus.NOT_FOUND, 
            "User Not Found", 
            ex.getMessage()
        );
    }

    /**
     * Maneja excepciones de credenciales inválidas durante el login
     * @return 401 UNAUTHORIZED
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.error("Credenciales inválidas: {}", ex.getMessage());
        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED, 
            "Invalid credentials", 
            "Email or password is incorrect"
        );
    }

    /**
     * Maneja excepciones cuando el email ya existe en el sistema
     * @return 409 CONFLICT
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        log.error("Email ya existe: {}", ex.getMessage());
        return buildErrorResponse(
            HttpStatus.CONFLICT, 
            "Email Already Exists", 
            ex.getMessage()
        );
    }

    /**
     * Maneja excepciones cuando el username ya existe en el sistema
     * @return 409 CONFLICT
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        log.error("Username ya existe: {}", ex.getMessage());
        return buildErrorResponse(
            HttpStatus.CONFLICT, 
            "Username Already Exists", 
            ex.getMessage()
        );
    }

    /**
     * Maneja excepciones de validación de datos (Bean Validation)
     * @return 400 BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.append(error.getField())
                  .append(": ")
                  .append(error.getDefaultMessage())
                  .append("; ")
        );
        
        log.error("Error de validación: {}", errors.toString());
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST, 
            "Validation Error", 
            errors.toString()
        );
    }

    /**
     * Maneja excepciones genéricas no capturadas por otros handlers
     * @return 500 INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error interno del servidor: ", ex);
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Internal Server Error", 
            "Ha ocurrido un error interno del servidor. Por favor, intente nuevamente más tarde."
        );
    }

    /**
     * Construye la respuesta de error en formato JSON estándar
     * 
     * Formato:
     * {
     *   "timestamp": "2025-11-16T10:30:00",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Descripción del error"
     * }
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, 
            String error, 
            String message) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        
        return ResponseEntity.status(status).body(errorResponse);
    }
}
