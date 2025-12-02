package com.congdinh.cms.services;

import java.util.List;

import com.congdinh.cms.dtos.news.NewsDetailDTO;
import com.congdinh.cms.dtos.news.NewsRequestDTO;
import com.congdinh.cms.dtos.news.NewsResponseDTO;

/**
 * Service interface for News operations.
 */
public interface NewsService {

    /**
     * Get all news articles.
     * - For authenticated users: returns all news
     * - For guests: returns only PUBLISHED news
     */
    List<NewsResponseDTO> getAllNews(boolean isAuthenticated);

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
