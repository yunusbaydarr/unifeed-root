import { Plus } from 'lucide-react';
import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';
import { Avatar, Button } from '../ui';
import type { NavigationItem, ShellUser } from './types';

export interface SidebarProps {
  items: NavigationItem[];
  user?: ShellUser;
  onCreatePost?: () => void;
  createPostLabel?: string;
  onProfile?: () => void;
  className?: string;
}

export function Sidebar({ items, user, onCreatePost, createPostLabel, onProfile, className }: SidebarProps) {
  const { t } = useTranslation();
  const resolvedCreatePostLabel = createPostLabel ?? t('feed.createPost');
  return (
    <aside className={clsx('fixed inset-y-0 left-0 z-30 hidden w-72 flex-col border-r border-border bg-surface/95 px-5 py-7 backdrop-blur lg:flex', className)} aria-label={t('a11y.mainNavigation')}>
      <a href="/" className="inline-flex flex-col rounded-lg px-1 focus-visible:outline-offset-4" aria-label={t('a11y.homeLink')}>
        <span className="font-heading text-4xl font-extrabold leading-none tracking-[-0.04em] text-primary">UniFeed</span>
        <span className="mt-1 text-sm text-muted-foreground">{t('brand.hub')}</span>
      </a>
      <nav className="mt-8 flex flex-col gap-1" aria-label={t('a11y.mainMenu')}>
        {items.map(({ icon: Icon, ...item }) => (
          <a key={item.href} href={item.href} onClick={item.onClick} aria-current={item.active ? 'page' : undefined} className={clsx('relative flex min-h-12 items-center gap-3 rounded-lg px-3 text-base font-medium transition-colors', item.active ? 'bg-primary/10 text-primary after:absolute after:inset-y-1 after:right-0 after:w-1 after:rounded-l-full after:bg-primary' : 'text-foreground hover:bg-muted')}>
            <Icon className="size-5 shrink-0" aria-hidden="true" />
            <span className="flex-1">{item.label}</span>
            {item.badge !== undefined && <span className="grid min-w-5 place-items-center rounded-full bg-tertiary px-1.5 text-xs font-semibold text-white">{item.badge}</span>}
          </a>
        ))}
      </nav>
      <div className="mt-auto grid gap-4">
        {onCreatePost && <Button className="w-full" leftIcon={<Plus className="size-5" />} onClick={onCreatePost}>{resolvedCreatePostLabel}</Button>}
        {user && <button type="button" onClick={onProfile} className="flex w-full items-center gap-3 rounded-xl p-2 text-left hover:bg-muted"><Avatar src={user.avatarUrl} alt={user.name} name={user.name} size="sm" /><div className="min-w-0"><p className="truncate text-sm font-semibold">{user.name}</p>{user.handle && <p className="truncate text-xs text-muted-foreground">{user.handle}</p>}</div></button>}
      </div>
    </aside>
  );
}
