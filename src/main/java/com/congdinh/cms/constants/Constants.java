package com.congdinh.cms.constants;

/**
 * Application-wide constants.
 */
public final class Constants {
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
    
    // Role constants
    public static final String ROLE_REPORTER = "ROLE_REPORTER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_EDITOR = "ROLE_EDITOR";
    
    // Token constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
}
