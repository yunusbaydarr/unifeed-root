import { MutationCache, QueryClient } from '@tanstack/react-query';
import type { FeedParams, UUID } from './types';

export const queryClient = new QueryClient({
  mutationCache: new MutationCache({ onError: (_error, _variables, _context, mutation) => {
    if (mutation.options.onError) return;
    if (typeof window !== 'undefined') window.dispatchEvent(new CustomEvent('unifeed:mutation-error'));
  } }),
  defaultOptions: {
    queries: { staleTime: 30_000, retry: (count, error) => count < 2 && !(typeof error === 'object' && error !== null && 'status' in error && error.status === 404), refetchOnWindowFocus: false },
    mutations: { retry: false },
  },
});

export const queryKeys = {
  all: ['unifeed'] as const,
  me: () => [...queryKeys.all, 'me'] as const,
  profile: (id: UUID) => [...queryKeys.all, 'profile', id] as const,
  userPosts: (id: UUID) => [...queryKeys.profile(id), 'posts'] as const,
  feed: (params?: FeedParams) => [...queryKeys.all, 'feed', params ?? {}] as const,
  post: (id: UUID) => [...queryKeys.all, 'post', id] as const,
  comments: (id: UUID) => [...queryKeys.post(id), 'comments'] as const,
  stories: () => [...queryKeys.all, 'stories'] as const,
  clubs: (params?: object) => [...queryKeys.all, 'clubs', params ?? {}] as const,
  club: (id: UUID) => [...queryKeys.all, 'club', id] as const,
  events: (params?: object) => [...queryKeys.all, 'events', params ?? {}] as const,
  event: (id: UUID) => [...queryKeys.all, 'event', id] as const,
  search: (query: string) => [...queryKeys.all, 'search', query] as const,
  chatThreads: () => [...queryKeys.all, 'chat', 'threads'] as const,
  chatMessages: (threadId: UUID) => [...queryKeys.all, 'chat', threadId, 'messages'] as const,
  notifications: (filter: string) => [...queryKeys.all, 'notifications', filter] as const,
};
