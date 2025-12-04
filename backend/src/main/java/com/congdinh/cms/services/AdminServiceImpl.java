package com.congdinh.cms.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.cms.dtos.admin.DashboardDTO;
import com.congdinh.cms.dtos.admin.DashboardDTO.CategoryStatDTO;
import com.congdinh.cms.enums.NewsStatus;
import com.congdinh.cms.repositories.NewsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AdminService.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final NewsRepository newsRepository;

    @Override
    public DashboardDTO getDashboardStats() {
        log.debug("Getting dashboard statistics");
        
        // Total news count
        long totalNews = newsRepository.countTotal();
        
        // Count by status
        Map<String, Long> newsByStatus = new LinkedHashMap<>();
        for (NewsStatus status : NewsStatus.values()) {
            long count = newsRepository.countByStatus(status);
            newsByStatus.put(status.name(), count);
        }
        
        // Top categories (limit to top 5)
        List<Object[]> categoryStats = newsRepository.countByCategory();
        List<CategoryStatDTO> topCategories = categoryStats.stream()
                .limit(5)
                .map(row -> CategoryStatDTO.builder()
                        .categoryId((Long) row[0])
                        .categoryName((String) row[1])
                        .articleCount((Long) row[2])
                        .build())
                .toList();
        
        log.info("Dashboard stats: total={}, byStatus={}", totalNews, newsByStatus);
        
        return DashboardDTO.builder()
                .totalNews(totalNews)
                .newsByStatus(newsByStatus)
                .topCategories(topCategories)
                .build();
    }
}
