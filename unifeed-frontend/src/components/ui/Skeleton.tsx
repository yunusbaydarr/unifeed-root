import { type HTMLAttributes } from 'react';
import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';

export function Skeleton({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div aria-hidden="true" className={clsx('animate-pulse rounded-md bg-muted', className)} {...props} />;
}

export function CardSkeleton() {
  const { t } = useTranslation();
  return (
    <div aria-label={t('common.loading')} role="status" className="rounded-xl border border-border bg-card p-4 shadow-card">
      <div className="flex items-center gap-3"><Skeleton className="size-11 rounded-full" /><div className="flex-1 space-y-2"><Skeleton className="h-3 w-32" /><Skeleton className="h-3 w-20" /></div></div>
      <div className="mt-4 space-y-2"><Skeleton className="h-4 w-full" /><Skeleton className="h-4 w-4/5" /></div>
      <Skeleton className="mt-4 aspect-video w-full rounded-lg" />
    </div>
  );
}
