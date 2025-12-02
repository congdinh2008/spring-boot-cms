package com.congdinh.cms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.congdinh.cms.entities.User;

/**
 * Repository for User entity.
 * Provides database access methods for User management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username.
     * Uses eager fetch for roles to avoid N+1 query problem.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    
    /**
     * Find a user by email.
     * Uses eager fetch for roles to avoid N+1 query problem.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * Check if a username already exists.
     * 
     * @param username the username to check
     * @return true if the username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists.
     * 
     * @param email the email to check
     * @return true if the email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if username or email already exists.
     * Useful for registration validation.
     * 
     * @param username the username to check
     * @param email the email to check
     * @return true if either username or email exists
     */
    boolean existsByUsernameOrEmail(String username, String email);
}
