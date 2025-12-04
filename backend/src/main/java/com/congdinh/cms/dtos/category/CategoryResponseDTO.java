package com.congdinh.cms.dtos.category;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for category response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Category information response")
public class CategoryResponseDTO {

    @Schema(description = "Unique identifier of the category", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Tin tức công nghệ")
    private String name;

    @Schema(description = "URL-friendly slug auto-generated from name", example = "tin-tuc-cong-nghe")
    private String slug;

    @Schema(description = "Timestamp when the category was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
