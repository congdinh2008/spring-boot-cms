package com.congdinh.cms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.congdinh.cms.entities.Category;

/**
 * Repository for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find category by name (case-insensitive).
     */
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Find category by slug.
     */
    Optional<Category> findBySlug(String slug);
    
    /**
     * Check if category name exists.
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Check if slug exists.
     */
    boolean existsBySlug(String slug);
    
    /**
     * Check if category name exists excluding a specific category (for update).
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
    
    /**
     * Check if category has associated news articles.
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM News n WHERE n.category.id = :categoryId")
    boolean hasNewsArticles(@Param("categoryId") Long categoryId);
}
