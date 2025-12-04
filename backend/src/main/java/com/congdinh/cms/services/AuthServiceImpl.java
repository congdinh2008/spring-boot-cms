package com.congdinh.cms.services;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.congdinh.cms.constants.Constants;
import com.congdinh.cms.dtos.auth.LoginRequestDTO;
import com.congdinh.cms.dtos.auth.LoginResponseDTO;
import com.congdinh.cms.dtos.auth.RegisterRequestDTO;
import com.congdinh.cms.dtos.auth.UserInformation;
import com.congdinh.cms.entities.Role;
import com.congdinh.cms.entities.User;
import com.congdinh.cms.enums.UserStatus;
import com.congdinh.cms.exceptions.AuthenticationException;
import com.congdinh.cms.exceptions.BadRequestException;
import com.congdinh.cms.exceptions.ResourceAlreadyExistsException;
import com.congdinh.cms.exceptions.ResourceNotFoundException;
import com.congdinh.cms.repositories.RoleRepository;
import com.congdinh.cms.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthService.
 * Handles user authentication and registration logic using Spring Security's AuthenticationManager.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    @Value("${jwt.expiration}")
    private Long jwtExpirationInSec;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.debug("Processing login request for user: {}", loginRequest.getUsername());
        
        try {
            // Use AuthenticationManager to authenticate - it will:
            // 1. Load user via UserDetailsService
            // 2. Verify password using PasswordEncoder
            // 3. Check account status (locked, disabled, etc.)
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            log.warn("Login failed - Invalid credentials for user: {}", loginRequest.getUsername());
            throw AuthenticationException.invalidCredentials();
        } catch (DisabledException e) {
            log.warn("Login failed - User account is disabled: {}", loginRequest.getUsername());
            throw AuthenticationException.accountDisabled();
        } catch (LockedException e) {
            log.warn("Login failed - User account is locked: {}", loginRequest.getUsername());
            throw AuthenticationException.accountLocked();
        }

        // If authentication succeeded, fetch user to build response
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(AuthenticationException::userNotFound);

        log.info("User logged in successfully: {}", loginRequest.getUsername());
        return buildLoginResponse(user);
    }

    @Override
    public LoginResponseDTO register(RegisterRequestDTO registerRequest) {
        log.debug("Processing registration for user: {}", registerRequest.getUsername());
        
        // Validate passwords match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            log.warn("Registration failed - Passwords do not match for user: {}", registerRequest.getUsername());
            throw BadRequestException.passwordsDoNotMatch();
        }

        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration failed - Username already exists: {}", registerRequest.getUsername());
            throw ResourceAlreadyExistsException.usernameExists(registerRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed - Email already exists: {}", registerRequest.getEmail());
            throw ResourceAlreadyExistsException.emailExists(registerRequest.getEmail());
        }

        // Get default role from database
        Role defaultRole = roleRepository.findByName(Constants.ROLE_REPORTER)
                .orElseThrow(() -> {
                    log.error("Default role not found: {}", Constants.ROLE_REPORTER);
                    return ResourceNotFoundException.roleNotFound(Constants.ROLE_REPORTER);
                });

        // Create new user
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRoles(Set.of(defaultRole));
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(newUser);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return buildLoginResponse(savedUser);
    }

    /**
     * Builds a login response from a user entity.
     * Centralizes response building to avoid code duplication.
     * 
     * @param user the authenticated user
     * @return LoginResponseDTO with token and user information
     */
    private LoginResponseDTO buildLoginResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String jwtToken = tokenService.generateToken(user, roleNames);

        UserInformation userInfo = UserInformation.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .roles(roleNames)
                .build();

        return LoginResponseDTO.builder()
                .token(jwtToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationInSec)
                .user(userInfo)
                .build();
    }
}
