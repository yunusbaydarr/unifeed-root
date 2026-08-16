import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { CheckCircle2, Info, TriangleAlert, X, XCircle } from 'lucide-react';
import { clsx } from 'clsx';
import { useTranslation } from 'react-i18next';

export type ToastVariant = 'info' | 'success' | 'warning' | 'danger';
export interface ToastInput { title: string; description?: string; variant?: ToastVariant; duration?: number }
interface ToastItem extends ToastInput { id: string }
interface ToastContextValue {
  toast: (input: ToastInput) => string;
  dismiss: (id: string) => void;
  info: (title: string, description?: string) => string;
  success: (title: string, description?: string) => string;
  warning: (title: string, description?: string) => string;
  error: (title: string, description?: string) => string;
}

const ToastContext = createContext<ToastContextValue | null>(null);

function ToastEntry({ item, onDismiss }: { item: ToastItem; onDismiss: (id: string) => void }) {
  const { t } = useTranslation();
  const config = {
    info: { icon: Info, className: 'text-primary' }, success: { icon: CheckCircle2, className: 'text-success dark:text-secondary' },
    warning: { icon: TriangleAlert, className: 'text-warning' }, danger: { icon: XCircle, className: 'text-danger' },
  }[item.variant ?? 'info'];
  const Icon = config.icon;
  useEffect(() => {
    const timeout = window.setTimeout(() => onDismiss(item.id), item.duration ?? 5000);
    return () => window.clearTimeout(timeout);
  }, [item.id, item.duration, onDismiss]);
  return (
    <div role={item.variant === 'danger' ? 'alert' : 'status'} className="glass-panel pointer-events-auto flex w-full items-start gap-3 rounded-xl p-4 sm:w-96">
      <Icon className={clsx('mt-0.5 size-5 shrink-0', config.className)} aria-hidden="true" />
      <div className="min-w-0 flex-1"><p className="text-sm font-semibold">{item.title}</p>{item.description && <p className="mt-1 text-sm text-muted-foreground">{item.description}</p>}</div>
      <button type="button" className="-mr-1 -mt-1 grid size-8 shrink-0 place-items-center rounded-full text-muted-foreground hover:bg-muted hover:text-foreground" aria-label={t('a11y.closeNotification')} onClick={() => onDismiss(item.id)}><X className="size-4" /></button>
    </div>
  );
}

export function ToastProvider({ children }: { children: ReactNode }) {
  const { t } = useTranslation();
  const [items, setItems] = useState<ToastItem[]>([]);
  const dismiss = useCallback((id: string) => setItems((current) => current.filter((item) => item.id !== id)), []);
  const toast = useCallback((input: ToastInput) => {
    const id = globalThis.crypto?.randomUUID?.() ?? `${Date.now()}-${Math.random()}`;
    setItems((current) => [...current, { ...input, id }].slice(-4));
    return id;
  }, []);
  useEffect(() => {
    const showMutationError = () => toast({ title: t('toast.mutationTitle'), description: t('toast.mutationDescription'), variant: 'danger' });
    window.addEventListener('unifeed:mutation-error', showMutationError);
    return () => window.removeEventListener('unifeed:mutation-error', showMutationError);
  }, [toast, t]);
  const value = useMemo(() => ({
    toast, dismiss,
    info: (title: string, description?: string) => toast({ title, description, variant: 'info' }),
    success: (title: string, description?: string) => toast({ title, description, variant: 'success' }),
    warning: (title: string, description?: string) => toast({ title, description, variant: 'warning' }),
    error: (title: string, description?: string) => toast({ title, description, variant: 'danger' }),
  }), [toast, dismiss]);
  return (
    <ToastContext.Provider value={value}>
      {children}
      {typeof document !== 'undefined' && createPortal(<div className="pointer-events-none fixed inset-x-4 top-4 z-[60] flex flex-col items-end gap-3 sm:inset-x-auto sm:right-5">{items.map((item) => <ToastEntry key={item.id} item={item} onDismiss={dismiss} />)}</div>, document.body)}
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast must be used within ToastProvider');
  return context;
}
