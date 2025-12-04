package com.congdinh.cms.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.congdinh.cms.dtos.ApiResponse;
import com.congdinh.cms.dtos.admin.DashboardDTO;
import com.congdinh.cms.dtos.news.NewsDetailDTO;
import com.congdinh.cms.services.AdminService;
import com.congdinh.cms.services.NewsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Admin operations.
 * Provides administrative functionalities like dashboard statistics and news
 * publishing.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin management APIs - Dashboard statistics and administrative operations. All endpoints require ADMIN role.")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final NewsService newsService;

    /**
     * Get dashboard statistics.
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Returns comprehensive dashboard statistics including: "
            +
            "total news count, news count grouped by status (DRAFT/PUBLISHED), " +
            "and top 5 most active categories based on news count. Requires ADMIN role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        DashboardDTO dashboard = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard retrieved successfully"));
    }

    /**
     * Publish a news article.
     * Changes status from DRAFT to PUBLISHED.
     */
    @PutMapping("/news/{id}/publish")
    @Operation(summary = "Publish a news article", description = "Changes a news article's status from DRAFT to PUBLISHED. "
            +
            "Published articles become visible to all users including guests. Requires ADMIN role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "News article published successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDetailDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Article is already published", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "News article not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<NewsDetailDTO>> publishNews(
            @Parameter(description = "News article ID to publish", required = true, example = "1") @PathVariable Long id) {
        NewsDetailDTO news = newsService.publishNews(id);
        return ResponseEntity.ok(ApiResponse.success(news, "News published successfully"));
    }
}
