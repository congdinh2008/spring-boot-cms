package com.congdinh.cms.dtos.news;

import java.time.LocalDateTime;

import com.congdinh.cms.enums.NewsStatus;

import lombok.*;

/**
 * DTO for news article response (list view).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponseDTO {
    
    private Long id;
    
    private String title;
    
    private NewsStatus status;
    
    private String categoryName;
    
    private Long categoryId;
    
    private String authorName;
    
    private Long authorId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
