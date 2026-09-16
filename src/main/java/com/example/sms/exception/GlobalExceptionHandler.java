package com.example.sms.exception;

import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class is the single place in the whole application where we
 * decide how exceptions get turned into HTTP responses.
 *
 * @RestControllerAdvice tells Spring "watch every controller in this
 * application, and if any of them throws one of the exceptions I
 * handle below, run my method instead of letting the request crash
 * with a generic 500 error."
 *
 * This is really convenient: our controllers and services can just
 * throw a NotFoundException or ConflictException whenever something
 * goes wrong, without needing to know anything about HTTP status codes
 * or JSON error formats. All of that is handled once, right here.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * A small record describing exactly what our error responses look
     * like as JSON, for example:
     * {
     *   "timestamp": "2026-09-16T20:00:00Z",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "Student not found: 42"
     * }
     */
    record ErrorResponse(Instant timestamp, int status, String error, String message) {
    }

    /**
     * Runs whenever a NotFoundException is thrown anywhere in a
     * controller or service (for example, StudentService.get() when the
     * requested student id doesn't exist).
     */
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Runs whenever a ConflictException is thrown (for example, trying
     * to create a student whose email is already taken).
     */
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ErrorResponse> handleConflict(ConflictException exception) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Runs automatically whenever a request body fails one of our
     * validation annotations (@NotBlank, @Email, @Min, @Max, etc. - see
     * Student.java and Course.java for examples). Spring throws this
     * exception for us; we don't have to trigger it ourselves.
     *
     * Instead of returning one generic "invalid input" message, we
     * collect every individual field error into a single readable
     * string, like:
     *   "email: must be a well-formed email address, gpa: must be less
     *    than or equal to 4.0"
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception) {
        String combinedMessage =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedMessage);
    }

    /**
     * Small helper so we don't repeat the same "build an ErrorResponse
     * with the current timestamp" logic in every handler method above.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse body =
                new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(body);
    }
}
