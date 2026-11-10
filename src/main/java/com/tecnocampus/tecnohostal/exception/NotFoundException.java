package com.tecnocampus.tecnohostal.exception;

/** El GlobalExceptionHandler la converteix en un 404 Not Found. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
