package com.congdinh.cms.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.cms.dtos.news.NewsDetailDTO;
import com.congdinh.cms.dtos.news.NewsRequestDTO;
import com.congdinh.cms.dtos.news.NewsResponseDTO;
import com.congdinh.cms.entities.Category;
import com.congdinh.cms.entities.News;
import com.congdinh.cms.entities.User;
import com.congdinh.cms.enums.NewsStatus;
import com.congdinh.cms.exceptions.BadRequestException;
import com.congdinh.cms.exceptions.ForbiddenException;
import com.congdinh.cms.exceptions.ResourceNotFoundException;
import com.congdinh.cms.repositories.CategoryRepository;
import com.congdinh.cms.repositories.NewsRepository;
import com.congdinh.cms.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of NewsService with ownership logic.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponseDTO> getAllNews(boolean isAuthenticated) {
        log.debug("Getting all news, authenticated: {}", isAuthenticated);
        
        List<News> newsList;
        if (isAuthenticated) {
            // Authenticated users see all news
            newsList = newsRepository.findAllWithDetails();
        } else {
            // Guests see only PUBLISHED news
            newsList = newsRepository.findByStatusWithDetails(NewsStatus.PUBLISHED);
        }
        
        return newsList.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDetailDTO getNewsById(Long id) {
        log.debug("Getting news by id: {}", id);
        
        News news = newsRepository.findByIdWithDetails(id)
                .orElseThrow(() -> ResourceNotFoundException.create("News", "id", id));
        
        return mapToDetailDTO(news);
    }

    @Override
    public NewsDetailDTO createNews(NewsRequestDTO request, Long authorId) {
        log.debug("Creating news with title: {} by author: {}", request.getTitle(), authorId);
        
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> ResourceNotFoundException.create("Category", "id", request.getCategoryId()));
        
        // Get author
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> ResourceNotFoundException.create("User", "id", authorId));
        
        // Create news with DRAFT status
        News news = News.builder()
                .title(request.getTitle().trim())
                .content(request.getContent())
                .status(NewsStatus.DRAFT)
                .category(category)
                .author(author)
                .build();
        
        News savedNews = newsRepository.save(news);
        log.info("Created news: {} with status DRAFT", savedNews.getId());
        
        return mapToDetailDTO(savedNews);
    }

    @Override
    public NewsDetailDTO updateNews(Long id, NewsRequestDTO request, Long currentUserId) {
        log.debug("Updating news id: {} by user: {}", id, currentUserId);
        
        News news = newsRepository.findByIdWithDetails(id)
                .orElseThrow(() -> ResourceNotFoundException.create("News", "id", id));
        
        // Check ownership - only author can update
        if (!news.getAuthor().getId().equals(currentUserId)) {
            log.warn("User {} attempted to update news {} owned by {}", 
                    currentUserId, id, news.getAuthor().getId());
            throw ForbiddenException.ownershipViolation("articles");
        }
        
        // Validate category if changed
        if (!news.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.create("Category", "id", request.getCategoryId()));
            news.setCategory(newCategory);
        }
        
        news.setTitle(request.getTitle().trim());
        news.setContent(request.getContent());
        
        News updatedNews = newsRepository.save(news);
        log.info("Updated news: {}", updatedNews.getId());
        
        return mapToDetailDTO(updatedNews);
    }

    @Override
    public void deleteNews(Long id, Long currentUserId, boolean isAdmin) {
        log.debug("Deleting news id: {} by user: {}, isAdmin: {}", id, currentUserId, isAdmin);
        
        News news = newsRepository.findByIdWithDetails(id)
                .orElseThrow(() -> ResourceNotFoundException.create("News", "id", id));
        
        // Admin can delete any, Reporter can only delete own
        if (!isAdmin && !news.getAuthor().getId().equals(currentUserId)) {
            log.warn("User {} attempted to delete news {} owned by {}", 
                    currentUserId, id, news.getAuthor().getId());
            throw ForbiddenException.ownershipViolation("articles");
        }
        
        newsRepository.delete(news);
        log.info("Deleted news: {} by user: {}", id, currentUserId);
    }

    @Override
    public NewsDetailDTO publishNews(Long id) {
        log.debug("Publishing news id: {}", id);
        
        News news = newsRepository.findByIdWithDetails(id)
                .orElseThrow(() -> ResourceNotFoundException.create("News", "id", id));
        
        // Only DRAFT can be published
        if (news.getStatus() != NewsStatus.DRAFT) {
            throw BadRequestException.invalidRequest(
                    "Only DRAFT articles can be published. Current status: " + news.getStatus());
        }
        
        news.setStatus(NewsStatus.PUBLISHED);
        News publishedNews = newsRepository.save(news);
        log.info("Published news: {}", publishedNews.getId());
        
        return mapToDetailDTO(publishedNews);
    }

    /**
     * Map News entity to response DTO (list view).
     */
    private NewsResponseDTO mapToResponseDTO(News news) {
        return NewsResponseDTO.builder()
                .id(news.getId())
                .title(news.getTitle())
                .status(news.getStatus())
                .categoryId(news.getCategory().getId())
                .categoryName(news.getCategory().getName())
                .authorId(news.getAuthor().getId())
                .authorName(news.getAuthor().getUsername())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }

    /**
     * Map News entity to detail DTO (full content).
     */
    private NewsDetailDTO mapToDetailDTO(News news) {
        return NewsDetailDTO.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .status(news.getStatus())
                .categoryId(news.getCategory().getId())
                .categoryName(news.getCategory().getName())
                .categorySlug(news.getCategory().getSlug())
                .authorId(news.getAuthor().getId())
                .authorName(news.getAuthor().getUsername())
                .authorEmail(news.getAuthor().getEmail())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}
