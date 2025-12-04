import { Navigate, useLocation } from 'react-router-dom';
import { useIsAuthenticated } from '@/stores';
import { LoadingPage } from '@/components/ui';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const isAuthenticated = useIsAuthenticated();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}

interface AdminRouteProps {
  children: React.ReactNode;
}

export function AdminRoute({ children }: AdminRouteProps) {
  const isAuthenticated = useIsAuthenticated();
  const location = useLocation();

  // Get user roles from store
  const userRoles = JSON.parse(localStorage.getItem('auth-storage') || '{}')?.state?.user?.roles || [];
  const isAdmin = userRoles.includes('ROLE_ADMIN');

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (!isAdmin) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
