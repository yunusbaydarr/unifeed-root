import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios';
import { authSession } from '@/lib/auth-session';
import type { ApiProblem, AuthTokens } from './types';

declare module 'axios' {
  interface InternalAxiosRequestConfig { _authRetry?: boolean }
}

const configuredBaseUrl = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/$/, '');
const apiBaseUrl = configuredBaseUrl?.endsWith('/api/v1')
  ? configuredBaseUrl.slice(0, -7)
  : (configuredBaseUrl ?? '');

export const apiClient = axios.create({
  baseURL: apiBaseUrl,
  withCredentials: true,
  headers: { Accept: 'application/json' },
});

const refreshClient = axios.create({ baseURL: apiBaseUrl, withCredentials: true });
let refreshInFlight: Promise<string> | null = null;

const refreshAccessToken = (): Promise<string> => {
  if (!refreshInFlight) {
    refreshInFlight = refreshClient.post<AuthTokens>('/api/v1/auth/refresh').then(({ data }) => {
      authSession.setAccessToken(data.accessToken);
      return data.accessToken;
    }).catch((error: unknown) => {
      authSession.setAccessToken(null);
      throw error;
    }).finally(() => { refreshInFlight = null; });
  }
  return refreshInFlight;
};

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  if (config.baseURL?.endsWith('/api/v1') && config.url?.startsWith('/api/v1/')) config.url = config.url.slice('/api/v1'.length);
  const accessToken = authSession.getAccessToken();
  if (accessToken) config.headers.set('Authorization', `Bearer ${accessToken}`);
  const language = typeof document === 'undefined' ? undefined : document.documentElement.lang;
  if (language) config.headers.set('Accept-Language', language);
  return config;
});

apiClient.interceptors.response.use(undefined, async (error: AxiosError<ApiProblem>) => {
  const request = error.config;
  const isAuthRequest = request?.url?.startsWith('/api/v1/auth/') ?? false;
  if (error.response?.status !== 401 || !request || request._authRetry || isAuthRequest) throw error;
  request._authRetry = true;
  const accessToken = await refreshAccessToken();
  request.headers.set('Authorization', `Bearer ${accessToken}`);
  return apiClient(request);
});

export const tryRestoreSession = async (): Promise<AuthTokens | null> => {
  try {
    const { data } = await refreshClient.post<AuthTokens>('/api/v1/auth/refresh');
    authSession.setAccessToken(data.accessToken);
    return data;
  } catch {
    authSession.setAccessToken(null);
    return null;
  }
};

export const clearApiSession = (): void => authSession.setAccessToken(null);

export const api = apiClient;
export const apiRequest = async <T>(config: AxiosRequestConfig): Promise<T> =>
  (await apiClient.request<T>(config)).data;
