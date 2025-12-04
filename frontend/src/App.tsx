import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Layout } from '@/components/layout';
import { ProtectedRoute, AdminRoute } from '@/components/auth';
import {
  HomePage,
  NotFoundPage,
  LoginPage,
  RegisterPage,
  CategoryListPage,
  NewsListPage,
  NewsDetailPage,
  MyNewsPage,
  AdminDashboardPage,
} from '@/pages';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            {/* Public routes */}
            <Route index element={<HomePage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
            <Route path="news" element={<NewsListPage />} />
            <Route path="news/:id" element={<NewsDetailPage />} />

            {/* Protected routes */}
            <Route
              path="categories"
              element={
                <ProtectedRoute>
                  <CategoryListPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="my-news"
              element={
                <ProtectedRoute>
                  <MyNewsPage />
                </ProtectedRoute>
              }
            />

            {/* Admin routes */}
            <Route
              path="admin"
              element={
                <AdminRoute>
                  <AdminDashboardPage />
                </AdminRoute>
              }
            />

            {/* 404 */}
            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
