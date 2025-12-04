package com.congdinh.cms.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception for forbidden access (403).
 */
public class ForbiddenException extends BaseException {

    private static final String ERROR_CODE = "AUTH_003";

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, ERROR_CODE);
    }

    /**
     * Create a forbidden exception with a custom message.
     */
    public static ForbiddenException forbidden(String message) {
        return new ForbiddenException(message);
    }

    /**
     * Create a forbidden exception for ownership violation.
     */
    public static ForbiddenException ownershipViolation(String resource) {
        return new ForbiddenException("You can only modify your own " + resource);
    }
}
