import { type HTMLAttributes, type ReactNode } from 'react';
import { AlertCircle, CheckCircle2, Info, TriangleAlert } from 'lucide-react';
import { clsx } from 'clsx';

export interface AlertProps extends HTMLAttributes<HTMLDivElement> {
  variant?: 'info' | 'success' | 'warning' | 'danger';
  title?: string;
  icon?: ReactNode;
}

export function Alert({ variant = 'info', title, icon, children, className, ...props }: AlertProps) {
  const config = {
    info: { className: 'border-primary/30 bg-primary/8 text-foreground', icon: <Info /> },
    success: { className: 'border-secondary/35 bg-secondary/10 text-foreground', icon: <CheckCircle2 /> },
    warning: { className: 'border-warning/35 bg-warning/10 text-foreground', icon: <TriangleAlert /> },
    danger: { className: 'border-danger/35 bg-danger/10 text-foreground', icon: <AlertCircle /> },
  }[variant];
  return (
    <div role={variant === 'danger' ? 'alert' : 'status'} className={clsx('flex gap-3 rounded-lg border-l-[3px] p-4 text-sm', config.className, className)} {...props}>
      <span className="mt-0.5 size-5 shrink-0 text-current [&>svg]:size-5">{icon ?? config.icon}</span>
      <div className="min-w-0">{title && <p className="mb-1 font-semibold">{title}</p>}<div className="text-muted-foreground">{children}</div></div>
    </div>
  );
}
