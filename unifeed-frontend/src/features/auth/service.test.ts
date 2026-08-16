import { beforeEach, describe, expect, it, vi } from 'vitest';

const apiClient = vi.hoisted(() => ({
  post: vi.fn(),
  get: vi.fn(),
}));
const clearApiSession = vi.hoisted(() => vi.fn());
const tryRestoreSession = vi.hoisted(() => vi.fn());

vi.mock('@/lib/api', () => ({ apiClient, clearApiSession, tryRestoreSession }));

import { authService } from './service';
import { useAuthStore } from './store';

describe('auth service contract', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useAuthStore.getState().setAnonymous();
  });

  it('uses API-version-relative auth paths', async () => {
    apiClient.post.mockResolvedValueOnce({ data: { accessToken: 'token', testAccount: false } });
    await authService.login({ email: 'student@uni.edu', password: 'secret123' });
    expect(apiClient.post).toHaveBeenCalledWith('/api/v1/auth/login', { email: 'student@uni.edu', password: 'secret123' });
  });

  it('always clears the local session when logout request fails', async () => {
    apiClient.post.mockRejectedValueOnce(new Error('offline'));
    await expect(authService.logout()).rejects.toThrow('offline');
    expect(clearApiSession).toHaveBeenCalledOnce();
    expect(useAuthStore.getState().status).toBe('anonymous');
  });
});
