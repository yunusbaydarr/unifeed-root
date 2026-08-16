import type { LucideIcon } from 'lucide-react';

export interface NavigationItem {
  label: string;
  href: string;
  icon: LucideIcon;
  active?: boolean;
  badge?: number | string;
  onClick?: () => void;
}

export interface ShellUser {
  name: string;
  handle?: string;
  avatarUrl?: string | null;
}
