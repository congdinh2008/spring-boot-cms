package com.congdinh.cms.dtos.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating or updating a news article.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsRequestDTO {
    
    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 50, message = "Content must be at least 50 characters")
    private String content;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
