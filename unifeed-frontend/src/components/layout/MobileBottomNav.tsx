import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';
import type { NavigationItem } from './types';

export interface MobileBottomNavProps { items: NavigationItem[]; className?: string }

export function MobileBottomNav({ items, className }: MobileBottomNavProps) {
  const { t } = useTranslation();
  return (
    <nav className={clsx('safe-bottom fixed inset-x-0 bottom-0 z-40 border-t border-border bg-card/95 backdrop-blur-lg lg:hidden', className)} aria-label={t('a11y.mobileNavigation')}>
      <div className="mx-auto grid h-16 max-w-2xl" style={{ gridTemplateColumns: `repeat(${items.length}, minmax(0, 1fr))` }}>
        {items.map(({ icon: Icon, ...item }) => (
          <a key={item.href} href={item.href} onClick={item.onClick} aria-current={item.active ? 'page' : undefined} className={clsx('relative flex min-w-0 flex-col items-center justify-center gap-0.5 px-1 text-[10px] font-semibold transition-colors', item.active ? 'text-primary' : 'text-muted-foreground hover:text-foreground')}>
            <span className={clsx('relative grid h-7 min-w-10 place-items-center rounded-full px-2', item.active && 'bg-primary/10')}><Icon className="size-5" aria-hidden="true" />{item.badge !== undefined && <span className="absolute -right-0.5 -top-1 grid min-w-4 place-items-center rounded-full bg-tertiary px-1 text-[9px] text-white">{item.badge}</span>}</span>
            <span className="max-w-full truncate">{item.label}</span>
          </a>
        ))}
      </div>
    </nav>
  );
}
