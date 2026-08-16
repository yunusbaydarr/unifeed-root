import { forwardRef, type ButtonHTMLAttributes, type ReactNode } from 'react';
import { clsx } from 'clsx';
import { Spinner } from './Spinner';
import { useTranslation } from 'react-i18next';

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger';
export type ButtonSize = 'sm' | 'md' | 'lg' | 'icon';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  leftIcon?: ReactNode;
  rightIcon?: ReactNode;
}

const variants: Record<ButtonVariant, string> = {
  primary: 'bg-primary text-primary-foreground shadow-[0_8px_24px_rgb(79_70_229/0.18)] hover:bg-primary-hover',
  secondary: 'bg-secondary text-secondary-foreground hover:brightness-95',
  outline: 'border-[1.5px] border-input bg-card text-foreground hover:border-primary hover:bg-surface-low',
  ghost: 'bg-transparent text-foreground hover:bg-muted',
  danger: 'bg-danger text-danger-foreground hover:brightness-90',
};

const sizes: Record<ButtonSize, string> = {
  sm: 'min-h-9 px-4 text-sm',
  md: 'min-h-11 px-5 text-sm',
  lg: 'min-h-12 px-6 text-base',
  icon: 'size-11 p-0',
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(function Button(
  { className, variant = 'primary', size = 'md', loading = false, leftIcon, rightIcon, disabled, children, type = 'button', ...props },
  ref,
) {
  const { t } = useTranslation();
  return (
    <button
      ref={ref}
      type={type}
      disabled={disabled || loading}
      aria-busy={loading || undefined}
      className={clsx(
        'inline-flex shrink-0 items-center justify-center gap-2 rounded-full font-semibold transition-[background-color,border-color,color,box-shadow,transform] duration-200 active:scale-[0.98] disabled:pointer-events-none disabled:opacity-50',
        variants[variant],
        sizes[size],
        className,
      )}
      {...props}
    >
      {loading ? <Spinner size="sm" aria-label={t('common.loading')} /> : leftIcon}
      {children}
      {!loading && rightIcon}
    </button>
  );
});
