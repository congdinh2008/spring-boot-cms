package com.congdinh.cms.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends BaseException {
    
    private static final String DEFAULT_ERROR_CODE = "NOT_FOUND_001";
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, DEFAULT_ERROR_CODE);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
              HttpStatus.NOT_FOUND, DEFAULT_ERROR_CODE);
    }
    
    /**
     * Generic factory method to create ResourceNotFoundException.
     */
    public static ResourceNotFoundException create(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceNotFoundException(resourceName, fieldName, fieldValue);
    }
    
    public static ResourceNotFoundException roleNotFound(String roleName) {
        return new ResourceNotFoundException("Role", "name", roleName);
    }
    
    public static ResourceNotFoundException userNotFound(String username) {
        return new ResourceNotFoundException("User", "username", username);
    }
    
    public static ResourceNotFoundException userNotFoundById(Long id) {
        return new ResourceNotFoundException("User", "id", id);
    }
    
    public static ResourceNotFoundException categoryNotFound(Long id) {
        return new ResourceNotFoundException("Category", "id", id);
    }
    
    public static ResourceNotFoundException newsNotFound(Long id) {
        return new ResourceNotFoundException("News", "id", id);
    }
}
