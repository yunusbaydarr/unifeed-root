import { type ReactNode } from 'react';
import { clsx } from 'clsx';

export interface EmptyStateProps {
  icon?: ReactNode;
  title: string;
  description?: string;
  action?: ReactNode;
  className?: string;
}

export function EmptyState({ icon, title, description, action, className }: EmptyStateProps) {
  return (
    <div className={clsx('flex min-h-64 flex-col items-center justify-center rounded-xl border border-dashed border-border bg-card/60 px-6 py-12 text-center', className)}>
      {icon && <div className="mb-4 grid size-14 place-items-center rounded-full bg-primary/10 text-primary [&>svg]:size-7">{icon}</div>}
      <h2 className="font-heading text-xl font-bold">{title}</h2>
      {description && <p className="mt-2 max-w-md text-sm text-muted-foreground">{description}</p>}
      {action && <div className="mt-5">{action}</div>}
    </div>
  );
}
