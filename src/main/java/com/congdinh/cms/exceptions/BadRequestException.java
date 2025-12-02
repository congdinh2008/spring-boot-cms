package com.congdinh.cms.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the request is invalid or malformed.
 * This includes validation errors, missing required fields, etc.
 */
public class BadRequestException extends BaseException {
    
    private static final String DEFAULT_ERROR_CODE = "BAD_REQUEST_001";
    
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, DEFAULT_ERROR_CODE);
    }
    
    public BadRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
    
    public static BadRequestException passwordsDoNotMatch() {
        return new BadRequestException("Passwords do not match", "BAD_REQUEST_002");
    }
    
    public static BadRequestException invalidRequest(String details) {
        return new BadRequestException("Invalid request: " + details, "BAD_REQUEST_003");
    }
}
