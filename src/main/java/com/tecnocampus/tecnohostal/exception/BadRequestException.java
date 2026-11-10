package com.tecnocampus.tecnohostal.exception;

/** El GlobalExceptionHandler la converteix en un 400 Bad Request. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
