import apiClient from './client';
import type { ApiResponse, PaginatedResponse, Category, CategoryRequest, CategoryListParams } from '@/types';

export const categoriesApi = {
  getList: async (params?: CategoryListParams): Promise<ApiResponse<PaginatedResponse<Category>>> => {
    const response = await apiClient.get<ApiResponse<PaginatedResponse<Category>>>('/v1/categories', {
      params,
    });
    return response.data;
  },

  getById: async (id: string | number): Promise<ApiResponse<Category>> => {
    const response = await apiClient.get<ApiResponse<Category>>(`/v1/categories/${id}`);
    return response.data;
  },

  create: async (data: CategoryRequest): Promise<ApiResponse<Category>> => {
    const response = await apiClient.post<ApiResponse<Category>>('/v1/categories', data);
    return response.data;
  },

  update: async (id: string | number, data: CategoryRequest): Promise<ApiResponse<Category>> => {
    const response = await apiClient.put<ApiResponse<Category>>(`/v1/categories/${id}`, data);
    return response.data;
  },

  delete: async (id: string | number): Promise<ApiResponse<void>> => {
    const response = await apiClient.delete<ApiResponse<void>>(`/v1/categories/${id}`);
    return response.data;
  },
};

export default categoriesApi;
