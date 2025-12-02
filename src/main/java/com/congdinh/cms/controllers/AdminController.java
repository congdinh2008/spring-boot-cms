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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Admin operations.
 */
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin management APIs")
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
    @Operation(summary = "Get dashboard statistics", 
               description = "Returns total news count, count by status, and top active categories. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        DashboardDTO dashboard = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard retrieved successfully"));
    }

    /**
     * Publish a news article.
     * Changes status from DRAFT to PUBLISHED.
     */
    @PutMapping("/news/{id}/publish")
    @Operation(summary = "Publish a news article", 
               description = "Changes news status from DRAFT to PUBLISHED. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<NewsDetailDTO>> publishNews(@PathVariable Long id) {
        NewsDetailDTO news = newsService.publishNews(id);
        return ResponseEntity.ok(ApiResponse.success(news, "News published successfully"));
    }
}
