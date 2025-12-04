package com.congdinh.cms.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * This includes duplicate usernames, emails, etc.
 */
public class ResourceAlreadyExistsException extends BaseException {
    
    private static final String DEFAULT_ERROR_CODE = "CONFLICT_001";
    
    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, DEFAULT_ERROR_CODE);
    }
    
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' already exists", resourceName, fieldName, fieldValue),
              HttpStatus.CONFLICT, DEFAULT_ERROR_CODE);
    }
    
    public static ResourceAlreadyExistsException usernameExists(String username) {
        return new ResourceAlreadyExistsException("User", "username", username);
    }
    
    public static ResourceAlreadyExistsException emailExists(String email) {
        return new ResourceAlreadyExistsException("User", "email", email);
    }
}
