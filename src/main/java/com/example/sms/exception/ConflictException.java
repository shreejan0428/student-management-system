package com.example.sms.exception;

/**
 * Thrown whenever an action would break a business rule that isn't a
 * simple "not found" - for example:
 *   - creating a student with an email that's already in use
 *   - creating a course with a code that already exists
 *   - enrolling a student in a course they're already enrolled in
 *   - enrolling a student in a course that's already full
 *
 * Just like NotFoundException, this bubbles up to GlobalExceptionHandler,
 * which turns it into an HTTP 409 Conflict response.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
