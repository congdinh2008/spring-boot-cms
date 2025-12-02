package com.congdinh.cms.services;

import java.util.Set;

import org.springframework.security.core.Authentication;

import com.congdinh.cms.entities.User;

public interface TokenService {
    String generateToken(User user, Set<String> roles);

    Authentication getAuthenticationFromToken(String token);
}
