import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { queryKeys } from '@/lib/api';
import type { AuthTokens, LoginRequest, RegisterRequest, ResetPasswordRequest, VerifyOtpRequest } from '@/lib/api';
import { authService, bootstrapAuth, establishAuthSession } from './service';
import { useAuthStore } from './store';

const completeAuth = async (tokens: AuthTokens) => {
  const user = await establishAuthSession(tokens);
  return { tokens, user };
};

export const useAuth = () => {
  const user = useAuthStore((state) => state.user);
  const status = useAuthStore((state) => state.status);
  const testAccount = useAuthStore((state) => state.testAccount);
  return {
    user,
    status,
    isAuthenticated: status === 'authenticated',
    isRestoring: status === 'idle' || status === 'restoring',
    testAccount,
  };
};

export const useAuthBootstrap = () => useQuery({
  queryKey: queryKeys.me(),
  queryFn: bootstrapAuth,
  staleTime: Infinity,
  retry: false,
});

export const useLogin = () => {
  const client = useQueryClient();
  return useMutation({
    mutationFn: async (request: LoginRequest) => completeAuth(await authService.login(request)),
    onSuccess: ({ user }) => client.setQueryData(queryKeys.me(), user),
  });
};

export const useRegister = () => useMutation({ mutationFn: (request: RegisterRequest) => authService.register(request) });

export const useVerifyOtp = () => {
  const client = useQueryClient();
  return useMutation({
    mutationFn: async (request: VerifyOtpRequest) => completeAuth(await authService.verifyOtp(request)),
    onSuccess: ({ user }) => client.setQueryData(queryKeys.me(), user),
  });
};

export const useForgotPassword = () => useMutation({ mutationFn: (email: string) => authService.forgotPassword({ email }) });
export const useResetPassword = () => useMutation({ mutationFn: (request: ResetPasswordRequest) => authService.resetPassword(request) });

export const useLogout = () => {
  const client = useQueryClient();
  return useMutation({
    mutationFn: authService.logout,
    onSettled: () => client.clear(),
  });
};
