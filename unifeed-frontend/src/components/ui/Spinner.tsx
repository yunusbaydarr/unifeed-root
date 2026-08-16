import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';

export interface SpinnerProps extends React.HTMLAttributes<HTMLSpanElement> {
  size?: 'sm' | 'md' | 'lg';
}

export function Spinner({ size = 'md', className, ...props }: SpinnerProps) {
  const { t } = useTranslation();
  const sizes = { sm: 'size-4 border-2', md: 'size-6 border-2', lg: 'size-10 border-[3px]' };
  return (
    <span
      {...props}
      role="status"
      aria-label={props['aria-label'] ?? t('common.loading')}
      className={clsx('inline-block animate-spin rounded-full border-current border-r-transparent', sizes[size], className)}
    />
  );
}
