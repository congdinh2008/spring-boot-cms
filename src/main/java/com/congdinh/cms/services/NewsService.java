package com.congdinh.cms.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.congdinh.cms.dtos.PageResponseDTO;
import com.congdinh.cms.dtos.news.NewsDetailDTO;
import com.congdinh.cms.dtos.news.NewsRequestDTO;
import com.congdinh.cms.dtos.news.NewsResponseDTO;
import com.congdinh.cms.enums.NewsStatus;

/**
 * Service interface for News operations.
 */
public interface NewsService {

    /**
     * Get all news articles (no pagination).
     * - For authenticated users: returns all news
     * - For guests: returns only PUBLISHED news
     */
    List<NewsResponseDTO> getAllNews(boolean isAuthenticated);

    /**
     * Search news articles with filters and pagination.
     * - For authenticated users: can filter by any status
     * - For guests: only returns PUBLISHED news
     * 
     * @param keyword search keyword for title or content
     * @param categoryId filter by category ID
     * @param status filter by status (only for authenticated users)
     * @param authorId filter by author ID
     * @param isAuthenticated whether the user is authenticated
     * @param pageable pagination and sorting parameters
     * @return paginated list of news articles
     */
    PageResponseDTO<NewsResponseDTO> searchNews(
            String keyword,
            Long categoryId,
            NewsStatus status,
            Long authorId,
            boolean isAuthenticated,
            Pageable pageable);

    /**
     * Get news article by ID.
     */
    NewsDetailDTO getNewsById(Long id);

    /**
     * Create a new news article.
     * - Default status: DRAFT
     * - author_id from current user
     */
    NewsDetailDTO createNews(NewsRequestDTO request, Long authorId);

    /**
     * Update a news article.
     * - Only the author can update their own article
     */
    NewsDetailDTO updateNews(Long id, NewsRequestDTO request, Long currentUserId);

    /**
     * Delete a news article.
     * - Admin: can delete any article
     * - Reporter: can only delete their own articles
     */
    void deleteNews(Long id, Long currentUserId, boolean isAdmin);

    /**
     * Publish a news article (Admin only).
     * Changes status from DRAFT to PUBLISHED.
     */
    NewsDetailDTO publishNews(Long id);
}
