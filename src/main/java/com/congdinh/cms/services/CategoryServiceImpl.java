package com.congdinh.cms.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.congdinh.cms.dtos.category.CategoryRequestDTO;
import com.congdinh.cms.dtos.category.CategoryResponseDTO;
import com.congdinh.cms.entities.Category;
import com.congdinh.cms.exceptions.BadRequestException;
import com.congdinh.cms.exceptions.ResourceAlreadyExistsException;
import com.congdinh.cms.exceptions.ResourceNotFoundException;
import com.congdinh.cms.repositories.CategoryRepository;
import com.congdinh.cms.utils.SlugUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CategoryService.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        log.debug("Fetching category by id: {}", id);
        Category category = findCategoryById(id);
        return mapToResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO getCategoryBySlug(String slug) {
        log.debug("Fetching category by slug: {}", slug);
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> ResourceNotFoundException.create("Category", "slug", slug));
        return mapToResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        log.debug("Creating new category: {}", request.getName());
        
        // Check if name already exists
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            log.warn("Category name already exists: {}", request.getName());
            throw new ResourceAlreadyExistsException("Category", "name", request.getName());
        }
        
        // Generate slug
        String slug = generateUniqueSlug(request.getName());
        
        // Create category
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setSlug(slug);
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Created category: {} with slug: {}", savedCategory.getName(), savedCategory.getSlug());
        
        return mapToResponseDTO(savedCategory);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        log.debug("Updating category id: {} with name: {}", id, request.getName());
        
        Category category = findCategoryById(id);
        
        // Check if new name already exists for another category
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            log.warn("Category name already exists: {}", request.getName());
            throw new ResourceAlreadyExistsException("Category", "name", request.getName());
        }
        
        // Update name and regenerate slug
        String newSlug = generateUniqueSlug(request.getName(), id);
        
        category.setName(request.getName().trim());
        category.setSlug(newSlug);
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Updated category: {} with slug: {}", updatedCategory.getName(), updatedCategory.getSlug());
        
        return mapToResponseDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        log.debug("Deleting category id: {}", id);
        
        Category category = findCategoryById(id);
        
        // Check if category has news articles
        if (categoryRepository.hasNewsArticles(id)) {
            log.warn("Cannot delete category {} - has associated news articles", id);
            throw BadRequestException.invalidRequest(
                "Cannot delete category '" + category.getName() + "' because it contains news articles. " +
                "Please move or delete the articles first."
            );
        }
        
        categoryRepository.delete(category);
        log.info("Deleted category: {}", category.getName());
    }

    /**
     * Find category by ID or throw exception.
     */
    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.create("Category", "id", id));
    }

    /**
     * Generate a unique slug for the category name.
     */
    private String generateUniqueSlug(String name) {
        return generateUniqueSlug(name, null);
    }

    /**
     * Generate a unique slug for the category name, excluding a specific category ID.
     */
    private String generateUniqueSlug(String name, Long excludeId) {
        String baseSlug = SlugUtils.toSlug(name);
        String slug = baseSlug;
        int suffix = 1;
        
        while (isSlugTaken(slug, excludeId)) {
            slug = SlugUtils.toSlugWithSuffix(baseSlug, suffix++);
        }
        
        return slug;
    }

    /**
     * Check if slug is already taken by another category.
     */
    private boolean isSlugTaken(String slug, Long excludeId) {
        return categoryRepository.findBySlug(slug)
                .map(existing -> excludeId == null || !existing.getId().equals(excludeId))
                .orElse(false);
    }

    /**
     * Map Category entity to response DTO.
     */
    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
