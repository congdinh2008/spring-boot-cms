package com.congdinh.cms.dtos.news;

import java.time.LocalDateTime;

import com.congdinh.cms.enums.NewsStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for news article detail response (full content).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Complete news article details including full content")
public class NewsDetailDTO {

    @Schema(description = "Unique identifier of the news article", example = "1")
    private Long id;

    @Schema(description = "Title of the news article", example = "Apple ra mắt iPhone 16 với nhiều tính năng AI mới")
    private String title;

    @Schema(description = "Full content/body of the news article", example = "Apple vừa chính thức ra mắt dòng iPhone 16 mới...")
    private String content;

    @Schema(description = "Current status of the article", example = "PUBLISHED", allowableValues = { "DRAFT",
            "PUBLISHED" })
    private NewsStatus status;

    @Schema(description = "ID of the category", example = "1")
    private Long categoryId;

    @Schema(description = "Name of the category", example = "Công nghệ")
    private String categoryName;

    @Schema(description = "URL-friendly slug of the category", example = "cong-nghe")
    private String categorySlug;

    @Schema(description = "ID of the author", example = "2")
    private Long authorId;

    @Schema(description = "Full name of the author", example = "Nguyễn Văn A")
    private String authorName;

    @Schema(description = "Email of the author", example = "nguyenvana@example.com")
    private String authorEmail;

    @Schema(description = "Timestamp when the article was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the article was last updated", example = "2024-01-16T14:20:00")
    private LocalDateTime updatedAt;
}
