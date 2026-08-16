import { type HTMLAttributes } from 'react';
import { clsx } from 'clsx';

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'primary' | 'secondary' | 'success' | 'coral' | 'neutral' | 'outline';
}

export function Badge({ variant = 'primary', className, ...props }: BadgeProps) {
  const variants = {
    primary: 'bg-primary/10 text-primary',
    secondary: 'bg-secondary/15 text-success dark:text-secondary',
    success: 'bg-secondary/15 text-success dark:text-secondary',
    coral: 'bg-tertiary/15 text-tertiary-foreground dark:text-tertiary',
    neutral: 'bg-muted text-muted-foreground',
    outline: 'border border-border bg-transparent text-foreground',
  };
  return <span className={clsx('inline-flex min-h-6 items-center rounded-full px-2.5 py-0.5 text-xs font-semibold tracking-wide', variants[variant], className)} {...props} />;
}
