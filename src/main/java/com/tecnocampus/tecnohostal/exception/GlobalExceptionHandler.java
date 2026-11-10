package com.tecnocampus.tecnohostal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

/**
 * Converteix les excepcions en respostes HTTP amb el codi adequat.
 * Llança NotFoundException, ConflictException o BadRequestException i aquesta classe fa la resta.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // JSON mal format o una data que no és ISO (per exemple "01/12/2026")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed request body");
    }

    // Un paràmetre amb un valor que no encaixa, per exemple ?type=TRIPLE
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'");
    }

    // Una URL que no correspon a cap endpoint
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoEndpoint(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Endpoint not found");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now().toString(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(body);
    }
}
