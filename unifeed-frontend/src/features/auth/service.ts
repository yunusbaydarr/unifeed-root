import { apiClient, clearApiSession, tryRestoreSession } from '@/lib/api';
import type {
  AuthTokens,
  ForgotPasswordRequest,
  LoginRequest,
  Profile,
  RegisterRequest,
  RegisterResponse,
  ResetPasswordRequest,
  VerifyOtpRequest,
} from '@/lib/api';
import { useAuthStore } from './store';

const authPath = '/api/v1/auth';

export const authService = {
  register: async (request: RegisterRequest): Promise<RegisterResponse> =>
    (await apiClient.post<RegisterResponse>(`${authPath}/register`, request)).data,
  login: async (request: LoginRequest): Promise<AuthTokens> =>
    (await apiClient.post<AuthTokens>(`${authPath}/login`, request)).data,
  verifyOtp: async (request: VerifyOtpRequest): Promise<AuthTokens> =>
    (await apiClient.post<AuthTokens>(`${authPath}/verify-otp`, request)).data,
  forgotPassword: async (request: ForgotPasswordRequest): Promise<void> => {
    await apiClient.post(`${authPath}/forgot-password`, request);
  },
  resetPassword: async (request: ResetPasswordRequest): Promise<void> => {
    await apiClient.post(`${authPath}/reset-password`, request);
  },
  me: async (): Promise<Profile> => (await apiClient.get<Profile>('/api/v1/users/me')).data,
  logout: async (): Promise<void> => {
    try { await apiClient.post(`${authPath}/logout`); }
    finally {
      clearApiSession();
      useAuthStore.getState().setAnonymous();
    }
  },
};

let bootstrapInFlight: Promise<Profile | null> | null = null;

export const establishAuthSession = async (tokens: AuthTokens): Promise<Profile> => {
  useAuthStore.getState().setAuthenticated(tokens);
  try {
    const user = await authService.me();
    useAuthStore.getState().setUser(user);
    return user;
  } catch (error) {
    useAuthStore.getState().setAnonymous();
    throw error;
  }
};

export const bootstrapAuth = (): Promise<Profile | null> => {
  if (bootstrapInFlight) return bootstrapInFlight;
  useAuthStore.getState().beginRestore();
  bootstrapInFlight = tryRestoreSession().then(async (tokens) => {
    if (!tokens) {
      useAuthStore.getState().setAnonymous();
      return null;
    }
    return establishAuthSession(tokens);
  }).catch(() => {
    useAuthStore.getState().setAnonymous();
    return null;
  }).finally(() => { bootstrapInFlight = null; });
  return bootstrapInFlight;
};
