package com.congdinh.cms.dtos.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating or updating a category.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a category")
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Schema(description = "Name of the category. Slug will be auto-generated from this name.", example = "Tin tức công nghệ", minLength = 2, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
