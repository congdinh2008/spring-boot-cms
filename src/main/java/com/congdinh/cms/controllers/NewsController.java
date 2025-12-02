package com.congdinh.cms.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.congdinh.cms.dtos.ApiResponse;
import com.congdinh.cms.dtos.news.NewsDetailDTO;
import com.congdinh.cms.dtos.news.NewsRequestDTO;
import com.congdinh.cms.dtos.news.NewsResponseDTO;
import com.congdinh.cms.services.NewsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for News management.
 */
@RestController
@RequestMapping("/api/v1/news")
@Tag(name = "News", description = "News management APIs")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * Get all news articles.
     * - Guest: only PUBLISHED
     * - Authenticated: all news
     */
    @GetMapping
    @Operation(summary = "Get all news articles", 
               description = "Returns all news for authenticated users, only PUBLISHED for guests.")
    public ResponseEntity<ApiResponse<List<NewsResponseDTO>>> getAllNews(Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        List<NewsResponseDTO> news = newsService.getAllNews(isAuthenticated);
        return ResponseEntity.ok(ApiResponse.success(news, "News retrieved successfully"));
    }

    /**
     * Get news article by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get news article by ID", 
               description = "Returns full details of a news article.")
    public ResponseEntity<ApiResponse<NewsDetailDTO>> getNewsById(@PathVariable Long id) {
        NewsDetailDTO news = newsService.getNewsById(id);
        return ResponseEntity.ok(ApiResponse.success(news, "News retrieved successfully"));
    }

    /**
     * Create a new news article (Reporter only).
     */
    @PostMapping
    @PreAuthorize("hasRole('REPORTER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new news article", 
               description = "Creates a news article with DRAFT status. Requires REPORTER or ADMIN role.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<NewsDetailDTO>> createNews(
            @Valid @RequestBody NewsRequestDTO request,
            Authentication authentication) {
        
        Long authorId = getCurrentUserId(authentication);
        NewsDetailDTO news = newsService.createNews(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(news, "News created successfully"));
    }

    /**
     * Update a news article (Author only).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('REPORTER') or hasRole('ADMIN')")
    @Operation(summary = "Update a news article", 
               description = "Updates a news article. Only the author can update their own article.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<NewsDetailDTO>> updateNews(
            @PathVariable Long id,
            @Valid @RequestBody NewsRequestDTO request,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        NewsDetailDTO news = newsService.updateNews(id, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(news, "News updated successfully"));
    }

    /**
     * Delete a news article.
     * - Admin: can delete any
     * - Reporter: can only delete own
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REPORTER') or hasRole('ADMIN')")
    @Operation(summary = "Delete a news article", 
               description = "Deletes a news article. Admin can delete any, Reporter can only delete own.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteNews(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        newsService.deleteNews(id, currentUserId, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(null, "News deleted successfully"));
    }

    /**
     * Extract current user ID from authentication.
     */
    private Long getCurrentUserId(Authentication authentication) {
        // The subject in JWT is the user ID
        return Long.parseLong(authentication.getName());
    }

    /**
     * Check if user has a specific role.
     */
    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }
}
