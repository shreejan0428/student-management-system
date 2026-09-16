package com.example.sms.exception;

/**
 * Thrown whenever code tries to look up something by id (a student, a
 * course, an enrollment...) and no row with that id exists in the
 * database.
 *
 * We don't handle this exception where it's thrown. Instead, we let it
 * bubble all the way up to GlobalExceptionHandler, which catches it and
 * turns it into a proper HTTP 404 Not Found response. This keeps our
 * service classes focused on business logic instead of HTTP details.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
