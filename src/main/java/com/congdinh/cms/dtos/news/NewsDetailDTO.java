package com.congdinh.cms.dtos.news;

import java.time.LocalDateTime;

import com.congdinh.cms.enums.NewsStatus;

import lombok.*;

/**
 * DTO for news article detail response (full content).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDetailDTO {
    
    private Long id;
    
    private String title;
    
    private String content;
    
    private NewsStatus status;
    
    private Long categoryId;
    
    private String categoryName;
    
    private String categorySlug;
    
    private Long authorId;
    
    private String authorName;
    
    private String authorEmail;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
