package com.tecnocampus.tecnohostal.exception;

/** El GlobalExceptionHandler la converteix en un 409 Conflict. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
