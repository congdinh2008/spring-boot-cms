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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for News management.
 * Provides CRUD operations for news articles with role-based access control.
 */
@RestController
@RequestMapping("/api/v1/news")
@Tag(name = "News", description = "News management APIs - CRUD operations for news articles with role-based access control")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * Get all news articles.
     * - Guest: only PUBLISHED
     * - Authenticated: all news
     */
    @GetMapping
    @Operation(summary = "Get all news articles", description = "Returns all news articles. Guest users see only PUBLISHED articles, "
            +
            "authenticated users see all articles regardless of status.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "News articles retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NewsResponseDTO.class))))
    })
    public ResponseEntity<ApiResponse<List<NewsResponseDTO>>> getAllNews(Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        List<NewsResponseDTO> news = newsService.getAllNews(isAuthenticated);
        return ResponseEntity.ok(ApiResponse.success(news, "News retrieved successfully"));
    }

    /**
     * Get news article by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get news article by ID", description = "Returns full details of a news article including author information and category.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "News article retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDetailDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "News article not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<NewsDetailDTO>> getNewsById(
            @Parameter(description = "News article ID", required = true, example = "1") @PathVariable Long id) {
        NewsDetailDTO news = newsService.getNewsById(id);
        return ResponseEntity.ok(ApiResponse.success(news, "News retrieved successfully"));
    }

    /**
     * Create a new news article (Reporter only).
     */
    @PostMapping
    @PreAuthorize("hasRole('REPORTER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new news article", description = "Creates a news article with DRAFT status. The authenticated user becomes the author. "
            +
            "Requires REPORTER or ADMIN role.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "News article created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDetailDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data (validation errors)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires REPORTER or ADMIN role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<NewsDetailDTO>> createNews(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "News article creation request", required = true, content = @Content(schema = @Schema(implementation = NewsRequestDTO.class))) @Valid @RequestBody NewsRequestDTO request,
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
    @Operation(summary = "Update a news article", description = "Updates an existing news article. Only the original author can update their own article. "
            +
            "Updating resets the status to DRAFT. Requires REPORTER or ADMIN role.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "News article updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDetailDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data (validation errors)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the author of this article or insufficient role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "News article or Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<NewsDetailDTO>> updateNews(
            @Parameter(description = "News article ID", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "News article update request", required = true, content = @Content(schema = @Schema(implementation = NewsRequestDTO.class))) @Valid @RequestBody NewsRequestDTO request,
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
    @Operation(summary = "Delete a news article", description = "Deletes a news article permanently. ADMIN can delete any article, "
            +
            "REPORTER can only delete their own articles.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "News article deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the author of this article (for REPORTER) or insufficient role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "News article not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<Void>> deleteNews(
            @Parameter(description = "News article ID", required = true, example = "1") @PathVariable Long id,
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
