package com.congdinh.cms.services;

import java.util.List;

import com.congdinh.cms.dtos.category.CategoryRequestDTO;
import com.congdinh.cms.dtos.category.CategoryResponseDTO;

/**
 * Service interface for Category operations.
 */
public interface CategoryService {
    
    /**
     * Get all categories.
     * 
     * @return list of all categories
     */
    List<CategoryResponseDTO> getAllCategories();
    
    /**
     * Get a category by ID.
     * 
     * @param id the category ID
     * @return the category
     */
    CategoryResponseDTO getCategoryById(Long id);
    
    /**
     * Get a category by slug.
     * 
     * @param slug the category slug
     * @return the category
     */
    CategoryResponseDTO getCategoryBySlug(String slug);
    
    /**
     * Create a new category.
     * 
     * @param request the category data
     * @return the created category
     */
    CategoryResponseDTO createCategory(CategoryRequestDTO request);
    
    /**
     * Update an existing category.
     * 
     * @param id the category ID
     * @param request the updated category data
     * @return the updated category
     */
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request);
    
    /**
     * Delete a category.
     * 
     * @param id the category ID
     */
    void deleteCategory(Long id);
}
