package com.microservice.note.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedNoteAccessException extends RuntimeException {
    public UnauthorizedNoteAccessException(String message) {
        super(message);
    }
}