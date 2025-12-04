import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { newsApi } from '@/api';
import type { NewsRequest, NewsListParams } from '@/types';

export const NEWS_QUERY_KEY = 'news';
export const DASHBOARD_QUERY_KEY = 'dashboard';

// News list with optional filters
export function useNews(params?: NewsListParams) {
  return useQuery({
    queryKey: [NEWS_QUERY_KEY, params],
    queryFn: () => newsApi.getList(params),
    staleTime: 2 * 60 * 1000,
  });
}

export function useNewsDetail(id: string | number) {
  return useQuery({
    queryKey: [NEWS_QUERY_KEY, 'detail', id],
    queryFn: () => newsApi.getById(id),
    enabled: !!id,
  });
}

export function useCreateNews() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: NewsRequest) => newsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [NEWS_QUERY_KEY] });
    },
  });
}

export function useUpdateNews() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string | number; data: NewsRequest }) =>
      newsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [NEWS_QUERY_KEY] });
    },
  });
}

export function useDeleteNews() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string | number) => newsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [NEWS_QUERY_KEY] });
    },
  });
}

// Admin: Publish news
export function usePublishNews() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string | number) => newsApi.publish(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [NEWS_QUERY_KEY] });
      queryClient.invalidateQueries({ queryKey: [DASHBOARD_QUERY_KEY] });
    },
  });
}

// Admin: Dashboard stats
export function useDashboard() {
  return useQuery({
    queryKey: [DASHBOARD_QUERY_KEY],
    queryFn: () => newsApi.getDashboard(),
    staleTime: 1 * 60 * 1000,
  });
}
