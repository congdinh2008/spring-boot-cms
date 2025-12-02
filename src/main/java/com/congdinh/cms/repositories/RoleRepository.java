package com.congdinh.cms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.congdinh.cms.entities.Role;

/**
 * Repository for Role entity.
 * Provides database access methods for Role management.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find a role by its name.
     * 
     * @param name the role name (e.g., "ROLE_ADMIN", "ROLE_REPORTER")
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if a role exists by name.
     * 
     * @param name the role name
     * @return true if the role exists
     */
    boolean existsByName(String name);
}
