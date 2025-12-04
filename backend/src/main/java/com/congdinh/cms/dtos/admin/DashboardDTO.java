package com.congdinh.cms.dtos.admin;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for admin dashboard statistics.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Admin dashboard statistics including news counts and top categories")
public class DashboardDTO {

    @Schema(description = "Total number of news articles in the system", example = "150")
    private long totalNews;

    @Schema(description = "Count of news articles grouped by status (DRAFT, PUBLISHED)", example = "{\"DRAFT\": 45, \"PUBLISHED\": 105}")
    private Map<String, Long> newsByStatus;

    @Schema(description = "Top 5 categories with the most articles")
    private List<CategoryStatDTO> topCategories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Category statistics showing article count per category")
    public static class CategoryStatDTO {

        @Schema(description = "ID of the category", example = "1")
        private Long categoryId;

        @Schema(description = "Name of the category", example = "Công nghệ")
        private String categoryName;

        @Schema(description = "Number of articles in this category", example = "25")
        private Long articleCount;
    }
}
