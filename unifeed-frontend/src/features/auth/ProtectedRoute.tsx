import type { ReactNode } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth, useAuthBootstrap } from './hooks';

interface ProtectedRouteProps {
  children?: ReactNode;
  redirectTo?: string;
  fallback?: ReactNode;
}

export function ProtectedRoute({ children, redirectTo = '/login', fallback = null }: ProtectedRouteProps) {
  useAuthBootstrap();
  const { isAuthenticated, isRestoring } = useAuth();
  const location = useLocation();

  if (isRestoring) return fallback;
  if (!isAuthenticated) return <Navigate to={redirectTo} replace state={{ from: location }} />;
  return children ?? <Outlet />;
}
