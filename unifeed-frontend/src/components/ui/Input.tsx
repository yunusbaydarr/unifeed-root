import { forwardRef, useId, type InputHTMLAttributes, type ReactNode } from 'react';
import { clsx } from 'clsx';

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  hint?: string;
  error?: string;
  leadingIcon?: ReactNode;
  trailingIcon?: ReactNode;
  containerClassName?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  { id, label, hint, error, leadingIcon, trailingIcon, className, containerClassName, required, ...props },
  ref,
) {
  const generatedId = useId();
  const inputId = id ?? generatedId;
  const descriptionId = error || hint ? `${inputId}-description` : undefined;
  return (
    <div className={clsx('grid gap-1.5', containerClassName)}>
      {label && (
        <label htmlFor={inputId} className="text-sm font-semibold text-foreground">
          {label}{required && <span className="ml-1 text-danger" aria-hidden="true">*</span>}
        </label>
      )}
      <div className="relative">
        {leadingIcon && <span className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-muted-foreground">{leadingIcon}</span>}
        <input
          ref={ref}
          id={inputId}
          required={required}
          aria-invalid={Boolean(error)}
          aria-describedby={descriptionId}
          className={clsx(
            'min-h-12 w-full rounded-lg border-[1.5px] border-input bg-card px-4 text-base text-foreground shadow-sm transition-[border-color,box-shadow] placeholder:text-muted-foreground focus:border-primary focus:outline-none focus:ring-4 focus:ring-primary/10 disabled:cursor-not-allowed disabled:bg-muted disabled:opacity-70',
            leadingIcon && 'pl-11', trailingIcon && 'pr-11', error && 'border-danger focus:border-danger focus:ring-danger/10', className,
          )}
          {...props}
        />
        {trailingIcon && <span className="absolute inset-y-0 right-0 flex items-center pr-3.5 text-muted-foreground">{trailingIcon}</span>}
      </div>
      {(error || hint) && <p id={descriptionId} className={clsx('text-xs', error ? 'text-danger' : 'text-muted-foreground')}>{error ?? hint}</p>}
    </div>
  );
});
