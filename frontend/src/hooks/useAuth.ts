import { useMutation, useQueryClient } from '@tanstack/react-query';
import { authApi } from '@/api';
import { useAuthStore } from '@/stores';
import type { LoginRequest, RegisterRequest } from '@/types';
import { useNavigate } from 'react-router-dom';

export function useLogin() {
  const navigate = useNavigate();
  const { login: setLogin, setLoading } = useAuthStore();

  return useMutation({
    mutationFn: async (data: LoginRequest) => {
      setLoading(true);
      const response = await authApi.login(data);
      return response.data;
    },
    onSuccess: (data) => {
      setLogin(data, data.user);
      navigate('/');
    },
    onError: () => {
      setLoading(false);
    },
  });
}

export function useRegister() {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
    onSuccess: () => {
      navigate('/login');
    },
  });
}

export function useLogout() {
  const navigate = useNavigate();
  const { logout } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => authApi.logout(),
    onSuccess: () => {
      logout();
      queryClient.clear();
      navigate('/login');
    },
    onError: () => {
      // Even if API fails, logout locally
      logout();
      queryClient.clear();
      navigate('/login');
    },
  });
}

export function useProfile() {
  const { user, isAuthenticated } = useAuthStore();

  // User info is already stored from login, no need to fetch
  return {
    data: user,
    isLoading: false,
    isAuthenticated,
  };
}
