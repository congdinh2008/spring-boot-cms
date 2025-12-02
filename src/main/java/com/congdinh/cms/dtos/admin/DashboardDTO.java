package com.congdinh.cms.dtos.admin;

import java.util.List;
import java.util.Map;

import lombok.*;

/**
 * DTO for admin dashboard statistics.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {
    
    /**
     * Total number of news articles.
     */
    private long totalNews;
    
    /**
     * Count by status: DRAFT, PUBLISHED, HIDDEN
     */
    private Map<String, Long> newsByStatus;
    
    /**
     * Top categories with most articles.
     */
    private List<CategoryStatDTO> topCategories;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryStatDTO {
        private Long categoryId;
        private String categoryName;
        private Long articleCount;
    }
}
