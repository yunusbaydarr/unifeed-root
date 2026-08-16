import { forwardRef, useId, type TextareaHTMLAttributes } from 'react';
import { clsx } from 'clsx';

export interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  hint?: string;
  error?: string;
  showCount?: boolean;
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(function Textarea(
  { id, label, hint, error, showCount, maxLength, value, defaultValue, className, required, ...props }, ref,
) {
  const generatedId = useId();
  const textareaId = id ?? generatedId;
  const currentLength = typeof value === 'string' ? value.length : typeof defaultValue === 'string' ? defaultValue.length : 0;
  const descriptionId = error || hint ? `${textareaId}-description` : undefined;
  return (
    <div className="grid gap-1.5">
      <div className="flex items-end justify-between gap-4">
        {label && <label htmlFor={textareaId} className="text-sm font-semibold">{label}{required && <span className="ml-1 text-danger" aria-hidden="true">*</span>}</label>}
        {showCount && maxLength && <span className="text-xs text-muted-foreground" aria-live="polite">{currentLength}/{maxLength}</span>}
      </div>
      <textarea
        ref={ref}
        id={textareaId}
        required={required}
        maxLength={maxLength}
        value={value}
        defaultValue={defaultValue}
        aria-invalid={Boolean(error)}
        aria-describedby={descriptionId}
        className={clsx('min-h-32 w-full resize-y rounded-lg border-[1.5px] border-input bg-card px-4 py-3 text-base shadow-sm transition-[border-color,box-shadow] placeholder:text-muted-foreground focus:border-primary focus:outline-none focus:ring-4 focus:ring-primary/10 disabled:cursor-not-allowed disabled:bg-muted disabled:opacity-70', error && 'border-danger focus:border-danger focus:ring-danger/10', className)}
        {...props}
      />
      {(error || hint) && <p id={descriptionId} className={clsx('text-xs', error ? 'text-danger' : 'text-muted-foreground')}>{error ?? hint}</p>}
    </div>
  );
});
