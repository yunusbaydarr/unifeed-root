import { useEffect, useId, useRef, type ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';
import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';
import { Button } from './Button';

export interface DialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: string;
  description?: string;
  children: ReactNode;
  footer?: ReactNode;
  className?: string;
  closeLabel?: string;
}

export function Dialog({ open, onOpenChange, title, description, children, footer, className, closeLabel }: DialogProps) {
  const { t } = useTranslation();
  const resolvedCloseLabel = closeLabel ?? t('common.close');
  const titleId = useId();
  const descriptionId = useId();
  const panelRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const previousActive = document.activeElement as HTMLElement | null;
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    const panel = panelRef.current;
    const focusable = panel?.querySelector<HTMLElement>('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
    focusable?.focus();
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') onOpenChange(false);
      if (event.key !== 'Tab' || !panel) return;
      const elements = Array.from(panel.querySelectorAll<HTMLElement>('button:not(:disabled), [href], input:not(:disabled), select:not(:disabled), textarea:not(:disabled), [tabindex]:not([tabindex="-1"])'));
      const first = elements[0];
      const last = elements[elements.length - 1];
      if (!first || !last) return;
      if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
      else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
    };
    document.addEventListener('keydown', onKeyDown);
    return () => {
      document.body.style.overflow = previousOverflow;
      document.removeEventListener('keydown', onKeyDown);
      previousActive?.focus();
    };
  }, [open, onOpenChange]);

  if (!open) return null;
  return createPortal(
    <div className="fixed inset-0 z-50 flex items-end justify-center p-0 sm:items-center sm:p-4" role="presentation">
      <button type="button" aria-label={resolvedCloseLabel} className="absolute inset-0 cursor-default bg-slate-950/45 backdrop-blur-[2px]" onClick={() => onOpenChange(false)} />
      <div ref={panelRef} role="dialog" aria-modal="true" aria-labelledby={titleId} aria-describedby={description ? descriptionId : undefined} className={clsx('glass-panel relative z-10 max-h-[92dvh] w-full overflow-y-auto rounded-t-2xl p-5 sm:max-w-lg sm:rounded-2xl sm:p-6', className)}>
        <div className="flex items-start justify-between gap-4 pr-1">
          <div><h2 id={titleId} className="font-heading text-2xl font-bold tracking-tight">{title}</h2>{description && <p id={descriptionId} className="mt-1 text-sm text-muted-foreground">{description}</p>}</div>
          <Button variant="ghost" size="icon" className="-mr-2 -mt-2" aria-label={resolvedCloseLabel} onClick={() => onOpenChange(false)}><X className="size-5" /></Button>
        </div>
        <div className="mt-5">{children}</div>
        {footer && <div className="mt-6 flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">{footer}</div>}
      </div>
    </div>,
    document.body,
  );
}

export const Modal = Dialog;
