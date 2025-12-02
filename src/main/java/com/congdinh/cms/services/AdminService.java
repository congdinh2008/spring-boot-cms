package com.congdinh.cms.services;

import com.congdinh.cms.dtos.admin.DashboardDTO;

/**
 * Service interface for Admin operations.
 */
public interface AdminService {

    /**
     * Get dashboard statistics.
     * - Total news count
     * - News count by status
     * - Top active categories
     */
    DashboardDTO getDashboardStats();
}
