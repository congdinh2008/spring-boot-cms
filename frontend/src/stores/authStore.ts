import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthState, UserInformation, LoginResponse } from '@/types';

interface AuthActions {
  setUser: (user: UserInformation) => void;
  setTokens: (accessToken: string, refreshToken: string | null) => void;
  login: (response: LoginResponse, user: UserInformation) => void;
  logout: () => void;
  setLoading: (loading: boolean) => void;
}

type AuthStore = AuthState & AuthActions;

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  isLoading: false,
};

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      ...initialState,

      setUser: (user: UserInformation) =>
        set({ user, isAuthenticated: true }),

      setTokens: (accessToken: string, refreshToken: string | null) =>
        set({ accessToken, refreshToken }),

      login: (response: LoginResponse, user: UserInformation) =>
        set({
          user,
          accessToken: response.token,
          refreshToken: null, // Backend doesn't provide refresh token in current response
          isAuthenticated: true,
          isLoading: false,
        }),

      logout: () =>
        set({
          ...initialState,
        }),

      setLoading: (isLoading: boolean) =>
        set({ isLoading }),
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

// Selector hooks for better performance
export const useUser = () => useAuthStore((state) => state.user);
export const useIsAuthenticated = () => useAuthStore((state) => state.isAuthenticated);
export const useIsAdmin = () =>
  useAuthStore((state) => state.user?.roles?.includes('ROLE_ADMIN') ?? false);
