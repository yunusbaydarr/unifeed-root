import { type ReactNode } from 'react';
import { clsx } from 'clsx';
import { MobileBottomNav } from './MobileBottomNav';
import { Sidebar, type SidebarProps } from './Sidebar';
import { TopBar, type TopBarProps } from './TopBar';
import type { NavigationItem } from './types';

export interface AppShellProps {
  children: ReactNode;
  navigation: NavigationItem[];
  sidebar?: Omit<SidebarProps, 'items'>;
  topBar?: TopBarProps | false;
  aside?: ReactNode;
  className?: string;
  contentClassName?: string;
  fullWidth?: boolean;
}

export function AppShell({ children, navigation, sidebar, topBar = {}, aside, className, contentClassName, fullWidth = false }: AppShellProps) {
  return (
    <div className={clsx('min-h-dvh bg-background text-foreground', className)}>
          <Sidebar items={navigation} {...sidebar} />
      <div className="min-h-dvh lg:pl-72">
        {topBar !== false && <TopBar {...topBar} />}
        <div className={clsx('mx-auto flex w-full items-start gap-6 px-4 py-5 pb-24 sm:px-6 sm:py-6 lg:px-8 lg:pb-8', fullWidth ? 'max-w-none' : 'max-w-[1200px]')}>
          <main id="main-content" className={clsx('min-w-0 flex-1', contentClassName)}>{children}</main>
          {aside && <aside className="sticky top-24 hidden w-72 shrink-0 xl:block">{aside}</aside>}
        </div>
      </div>
      <MobileBottomNav items={navigation} />
    </div>
  );
}
