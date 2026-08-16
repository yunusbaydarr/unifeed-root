import { useEffect, useState, type FormEvent } from 'react';
import { Bell, CalendarDays, Compass, Home, LogOut, MessageCircle, Plus, UserRound } from 'lucide-react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { AppShell } from '@/components/layout';
import { Button, Dialog, Textarea, useToast } from '@/components/ui';
import { useAuth, useLogout } from '@/features/auth';
import { api } from '@/lib/api';
import { socketManager } from '@/lib/socket';
import { useNotifications, useThreads } from '@/features/data';

export function MainLayout() {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();
  const logout = useLogout();
  const queryClient = useQueryClient();
  const toast = useToast();
  const [composerOpen, setComposerOpen] = useState(false);
  const notifications = useNotifications('UNREAD');
  const threads = useThreads();
  const unreadMessages = threads.data?.reduce((total, thread) => total + thread.unreadCount, 0) ?? 0;
  useEffect(() => {
    socketManager.connect();
    return socketManager.subscribeToNotifications((notification) => {
      queryClient.setQueryData<unknown[]>(['notifications', 'UNREAD'], (old) => [notification, ...(old ?? [])]);
      void queryClient.invalidateQueries({ queryKey: ['notifications'] });
      void queryClient.invalidateQueries({ queryKey: ['chat', 'threads'] });
      toast.info(t('notifications.fallback'));
    });
  }, [queryClient, t, toast]);
  const createPost = useMutation({
    mutationFn: async ({ content, file }: { content: string; file?: File }) => {
      let mediaPaths: string[] = [];
      if (file) { const form = new FormData(); form.append('file', file); form.append('category', 'POST'); const uploaded = await api.post<{ originalPath: string }>('/api/v1/media', form); mediaPaths = [uploaded.data.originalPath]; }
      return api.post('/api/v1/posts', { content, mediaPaths });
    },
    onSuccess: async () => { await queryClient.invalidateQueries({ queryKey: ['feed'] }); setComposerOpen(false); toast.success(t('feed.published')); },
    onError: () => toast.error(t('feed.publishError')),
  });
  const navigation = [
    { label: t('nav.home'), href: '/', icon: Home },
    { label: t('nav.discover'), href: '/explore', icon: Compass },
    { label: t('nav.community'), href: '/community', icon: CalendarDays },
    { label: t('nav.messages'), href: '/messages', icon: MessageCircle, badge: unreadMessages || undefined },
    { label: t('nav.notifications'), href: '/notifications', icon: Bell, badge: notifications.data?.length || undefined },
    { label: t('nav.profile'), href: '/profile', icon: UserRound },
  ].map((item) => ({ ...item, active: item.href === '/' ? location.pathname === '/' : location.pathname.startsWith(item.href) }));
  const submit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const content = String(new FormData(event.currentTarget).get('content') ?? '').trim();
    const candidate = new FormData(event.currentTarget).get('media');
    if (content) createPost.mutate({ content, file: candidate instanceof File && candidate.size ? candidate : undefined });
  };
  return <>
    <AppShell
      navigation={navigation}
      sidebar={{ user: user ? { name: user.displayName, handle: user.department ?? user.email ?? undefined, avatarUrl: user.avatarPath } : undefined, onCreatePost: () => setComposerOpen(true), onProfile: () => navigate('/profile') }}
      topBar={{ onSearch: (q) => q && navigate(`/explore?q=${encodeURIComponent(q)}`), onNotifications: () => navigate('/notifications'), onSettings: () => navigate('/settings'), onProfile: () => navigate('/profile'), notificationCount: notifications.data?.length ?? 0, user: user ? { name: user.displayName, avatarUrl: user.avatarPath } : undefined, actions: <><Button className="lg:hidden" variant="ghost" size="icon" aria-label={t('a11y.createPost')} onClick={() => setComposerOpen(true)}><Plus className="size-5" /></Button><Button variant="ghost" size="icon" aria-label={t('a11y.logout')} onClick={() => logout.mutate(undefined, { onSettled: () => navigate('/auth/login') })}><LogOut className="size-5" /></Button></> }}
    ><Outlet /></AppShell>
    <Dialog open={composerOpen} onOpenChange={setComposerOpen} title={t('feed.createPost')} description={t('feed.createDescription')} footer={<><Button variant="ghost" onClick={() => setComposerOpen(false)}>{t('common.discard')}</Button><Button type="submit" form="composer" loading={createPost.isPending}>{t('common.publish')}</Button></>}>
      <form id="composer" onSubmit={submit} className="grid gap-4"><Textarea name="content" label={t('feed.composerLabel')} rows={5} maxLength={1000} required /><label className="grid gap-1.5 text-sm font-semibold">{t('feed.image')} <input name="media" type="file" accept="image/*" className="rounded-lg border border-input bg-card p-3 font-normal"/></label></form>
    </Dialog>
  </>;
}
