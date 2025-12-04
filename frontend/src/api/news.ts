import apiClient from './client';
import type { ApiResponse, PaginatedResponse, News, NewsDetail, NewsRequest, NewsListParams } from '@/types';

export const newsApi = {
  // Public/Auth endpoints - GET news list with pagination
  getList: async (params?: NewsListParams): Promise<ApiResponse<PaginatedResponse<News>>> => {
    const response = await apiClient.get<ApiResponse<PaginatedResponse<News>>>('/v1/news', {
      params,
    });
    return response.data;
  },

  // Get news by ID (returns full detail)
  getById: async (id: string | number): Promise<ApiResponse<NewsDetail>> => {
    const response = await apiClient.get<ApiResponse<NewsDetail>>(`/v1/news/${id}`);
    return response.data;
  },

  // Create news (Reporter/Admin)
  create: async (data: NewsRequest): Promise<ApiResponse<News>> => {
    const response = await apiClient.post<ApiResponse<News>>('/v1/news', data);
    return response.data;
  },

  // Update news (Author only)
  update: async (id: string | number, data: NewsRequest): Promise<ApiResponse<News>> => {
    const response = await apiClient.put<ApiResponse<News>>(`/v1/news/${id}`, data);
    return response.data;
  },

  // Delete news (Author or Admin)
  delete: async (id: string | number): Promise<ApiResponse<void>> => {
    const response = await apiClient.delete<ApiResponse<void>>(`/v1/news/${id}`);
    return response.data;
  },

  // Admin: Publish news
  publish: async (id: string | number): Promise<ApiResponse<News>> => {
    const response = await apiClient.put<ApiResponse<News>>(`/v1/admin/news/${id}/publish`);
    return response.data;
  },

  // Admin: Get dashboard stats
  getDashboard: async (): Promise<ApiResponse<DashboardStats>> => {
    const response = await apiClient.get<ApiResponse<DashboardStats>>('/v1/admin/dashboard');
    return response.data;
  },
};

// Dashboard stats type (DashboardDTO from backend)
export interface DashboardStats {
  totalNews: number;
  newsByStatus: Record<string, number>;
  topCategories: CategoryStat[];
}

// CategoryStatDTO from backend
export interface CategoryStat {
  categoryId: number;
  categoryName: string;
  articleCount: number;
}

export default newsApi;
