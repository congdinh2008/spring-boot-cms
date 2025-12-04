package com.congdinh.cms.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.congdinh.cms.dtos.ApiResponse;
import com.congdinh.cms.dtos.PageResponseDTO;
import com.congdinh.cms.dtos.category.CategoryRequestDTO;
import com.congdinh.cms.dtos.category.CategoryResponseDTO;
import com.congdinh.cms.services.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Category management.
 */
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management APIs - CRUD operations for news categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Get all categories (Public) - No pagination.
     */
    @GetMapping("/all")
    @Operation(summary = "Get all categories (no pagination)", description = "Returns a list of all categories without pagination. Useful for dropdowns and filters.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
    }

    /**
     * Search categories with pagination (Public).
     */
    @GetMapping
    @Operation(
        summary = "Search categories with pagination", 
        description = "Search categories by keyword (name or slug) with pagination and sorting. " +
                      "Default sort: createdAt DESC. Allowed sort fields: name, slug, createdAt."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<PageResponseDTO<CategoryResponseDTO>>> searchCategories(
            @Parameter(description = "Search keyword for name or slug", example = "công nghệ")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size (max 100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field (name, slug, createdAt)", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction (asc, desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        // Validate and limit page size
        size = Math.min(size, MAX_PAGE_SIZE);
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        
        // Validate sort field
        String validSortBy = validateSortField(sortBy, "name", "slug", "createdAt");
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(validSortBy).ascending() 
                : Sort.by(validSortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponseDTO<CategoryResponseDTO> result = categoryService.searchCategories(keyword, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(result, "Categories retrieved successfully"));
    }

    /**
     * Get category by ID (Public).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a single category by its ID. This endpoint is public.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(
            @Parameter(description = "Category ID", required = true, example = "1") @PathVariable Long id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category, "Category retrieved successfully"));
    }

    /**
     * Get category by slug (Public).
     */
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Returns a single category by its URL-friendly slug. This endpoint is public.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryBySlug(
            @Parameter(description = "Category slug (URL-friendly)", required = true, example = "tin-tuc") @PathVariable String slug) {
        CategoryResponseDTO category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(category, "Category retrieved successfully"));
    }

    /**
     * Create a new category (Admin only).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new category", description = "Creates a new category with auto-generated slug. Requires ADMIN role.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or category name already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category creation request", required = true, content = @Content(schema = @Schema(implementation = CategoryRequestDTO.class))) @Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(category, "Category created successfully"));
    }

    /**
     * Update an existing category (Admin only).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a category", description = "Updates an existing category by ID. Slug will be regenerated based on new name. Requires ADMIN role.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or category name already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
            @Parameter(description = "Category ID", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category update request", required = true, content = @Content(schema = @Schema(implementation = CategoryRequestDTO.class))) @Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success(category, "Category updated successfully"));
    }

    /**
     * Delete a category (Admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a category", description = "Deletes a category by ID. Cannot delete if category has associated news articles. Requires ADMIN role.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete - category has associated news articles", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "Category ID", required = true, example = "1") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }

    /**
     * Validate sort field against allowed fields.
     */
    private String validateSortField(String sortBy, String... allowedFields) {
        for (String field : allowedFields) {
            if (field.equalsIgnoreCase(sortBy)) {
                return field;
            }
        }
        return allowedFields[0]; // Default to first allowed field
    }
}
