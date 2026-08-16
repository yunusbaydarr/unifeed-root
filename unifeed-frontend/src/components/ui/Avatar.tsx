import { useMemo, useState, type HTMLAttributes } from 'react';
import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';

export interface AvatarProps extends HTMLAttributes<HTMLSpanElement> {
  src?: string | null;
  alt?: string;
  name?: string;
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  status?: 'online' | 'offline' | 'busy';
}

export function Avatar({ src, name, alt = name ?? '', size = 'md', status, className, ...props }: AvatarProps) {
  const { t } = useTranslation();
  const [failed, setFailed] = useState(false);
  const initials = useMemo(() => (name ?? alt).split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]?.toLocaleUpperCase('tr-TR')).join(''), [name, alt]);
  const sizes = { xs: 'size-7 text-[10px]', sm: 'size-9 text-xs', md: 'size-11 text-sm', lg: 'size-16 text-lg', xl: 'size-24 text-2xl' };
  const dotSizes = { xs: 'size-2', sm: 'size-2.5', md: 'size-3', lg: 'size-3.5', xl: 'size-4' };
  const statusColors = { online: 'bg-secondary', offline: 'bg-muted-foreground', busy: 'bg-tertiary' };
  return (
    <span className={clsx('relative inline-flex shrink-0', className)} {...props}>
      <span className={clsx('inline-flex items-center justify-center overflow-hidden rounded-full bg-primary/12 font-semibold text-primary ring-1 ring-border', sizes[size])}>
        {src && !failed ? <img src={src} alt={alt} className="size-full object-cover" onError={() => setFailed(true)} /> : <span aria-label={alt}>{initials || '?'}</span>}
      </span>
      {status && <span className={clsx('absolute bottom-0 right-0 rounded-full ring-2 ring-card', dotSizes[size], statusColors[status])} aria-label={t({ online: 'a11y.statusOnline', offline: 'a11y.statusOffline', busy: 'a11y.statusBusy' }[status])} />}
    </span>
  );
}
