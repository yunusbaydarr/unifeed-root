import { create } from 'zustand';
import { authSession } from '@/lib/auth-session';
import type { AuthTokens, Profile } from '@/lib/api';

export type AuthStatus = 'idle' | 'restoring' | 'authenticated' | 'anonymous';

interface AuthState {
  accessToken: string | null;
  user: Profile | null;
  testAccount: boolean;
  status: AuthStatus;
  beginRestore: () => void;
  setAuthenticated: (tokens: AuthTokens, user?: Profile | null) => void;
  setUser: (user: Profile) => void;
  setAnonymous: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  testAccount: false,
  status: 'idle',
  beginRestore: () => set({ status: 'restoring' }),
  setAuthenticated: (tokens, user = null) => {
    authSession.setAccessToken(tokens.accessToken);
    set({ accessToken: tokens.accessToken, user, testAccount: tokens.testAccount, status: 'authenticated' });
  },
  setUser: (user) => set({ user, testAccount: user.testAccount }),
  setAnonymous: () => {
    authSession.setAccessToken(null);
    set({ accessToken: null, user: null, testAccount: false, status: 'anonymous' });
  },
}));

authSession.subscribe((accessToken) => {
  const state = useAuthStore.getState();
  if (accessToken === state.accessToken) return;
  if (!accessToken) state.setAnonymous();
  else useAuthStore.setState({ accessToken, status: 'authenticated' });
});

export const authStore = useAuthStore;
