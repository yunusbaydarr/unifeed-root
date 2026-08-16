import { Bell, Menu, Search, Settings } from 'lucide-react';
import { type FormEvent, type ReactNode } from 'react';
import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';
import { Avatar, Button, Input } from '../ui';
import type { ShellUser } from './types';

export interface TopBarProps {
  title?: string;
  user?: ShellUser;
  searchPlaceholder?: string;
  onSearch?: (query: string) => void;
  onNotifications?: () => void;
  onSettings?: () => void;
  onMenu?: () => void;
  onProfile?: () => void;
  notificationCount?: number;
  actions?: ReactNode;
  className?: string;
}

export function TopBar({ title, user, searchPlaceholder, onSearch, onNotifications, onSettings, onMenu, onProfile, notificationCount, actions, className }: TopBarProps) {
  const { t } = useTranslation();
  const resolvedSearchPlaceholder = searchPlaceholder ?? t('search.placeholder');
  const submitSearch = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    onSearch?.(String(formData.get('query') ?? '').trim());
  };
  return (
    <header className={clsx('sticky top-0 z-20 flex min-h-16 items-center gap-3 border-b border-border bg-background/90 px-4 backdrop-blur-lg sm:px-6 lg:px-8', className)}>
      {onMenu && <Button variant="ghost" size="icon" className="lg:hidden" aria-label={t('a11y.openMenu')} onClick={onMenu}><Menu className="size-5" /></Button>}
      {title && <h1 className="truncate font-heading text-lg font-bold sm:text-xl">{title}</h1>}
      {onSearch && <form role="search" onSubmit={submitSearch} className={clsx('w-full max-w-xl', title && 'ml-auto hidden sm:block')}><Input name="query" type="search" aria-label={resolvedSearchPlaceholder} placeholder={resolvedSearchPlaceholder} leadingIcon={<Search className="size-5" />} className="min-h-10 rounded-full bg-surface-low" /></form>}
      {!title && !onSearch && <div className="flex-1" />}
      <div className="ml-auto flex shrink-0 items-center gap-1">
        {actions}
        {onNotifications && <Button variant="ghost" size="icon" aria-label={notificationCount ? t('a11y.notificationsCount', { count: notificationCount }) : t('nav.notifications')} className="relative" onClick={onNotifications}><Bell className="size-5" />{Boolean(notificationCount) && <span className="absolute right-2 top-2 size-2 rounded-full bg-tertiary ring-2 ring-background" />}</Button>}
        {onSettings && <Button variant="ghost" size="icon" aria-label={t('nav.settings')} onClick={onSettings}><Settings className="size-5" /></Button>}
        {user && <button type="button" aria-label={t('nav.profile')} onClick={onProfile} className="ml-1 rounded-full"><Avatar src={user.avatarUrl} alt={user.name} name={user.name} size="sm" /></button>}
      </div>
    </header>
  );
}
