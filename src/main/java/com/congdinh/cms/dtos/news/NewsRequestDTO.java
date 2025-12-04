package com.congdinh.cms.dtos.news;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for creating or updating a news article")
public class NewsRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
    @Schema(description = "Title of the news article", example = "Apple ra mắt iPhone 16 với nhiều tính năng AI mới", minLength = 10, maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 50, message = "Content must be at least 50 characters")
    @Schema(description = "Full content/body of the news article. Supports HTML formatting.", example = "Apple vừa chính thức ra mắt dòng iPhone 16 mới với nhiều cải tiến về AI và camera. Sản phẩm được kỳ vọng sẽ thay đổi cách người dùng tương tác với điện thoại thông minh.", minLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the category this news article belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;
}
