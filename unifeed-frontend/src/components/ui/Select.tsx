import { forwardRef, useId, type SelectHTMLAttributes } from 'react';
import { clsx } from 'clsx';
import { ChevronDown } from 'lucide-react';

export interface SelectOption { label: string; value: string; disabled?: boolean }
export interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  hint?: string;
  error?: string;
  placeholder?: string;
  options: SelectOption[];
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(function Select(
  { id, label, hint, error, placeholder, options, className, required, ...props }, ref,
) {
  const generatedId = useId();
  const selectId = id ?? generatedId;
  const descriptionId = error || hint ? `${selectId}-description` : undefined;
  return (
    <div className="grid gap-1.5">
      {label && <label htmlFor={selectId} className="text-sm font-semibold">{label}{required && <span className="ml-1 text-danger" aria-hidden="true">*</span>}</label>}
      <div className="relative">
        <select ref={ref} id={selectId} required={required} aria-invalid={Boolean(error)} aria-describedby={descriptionId} className={clsx('min-h-12 w-full appearance-none rounded-lg border-[1.5px] border-input bg-card px-4 pr-11 text-base shadow-sm focus:border-primary focus:outline-none focus:ring-4 focus:ring-primary/10 disabled:cursor-not-allowed disabled:bg-muted disabled:opacity-70', error && 'border-danger focus:border-danger focus:ring-danger/10', className)} {...props}>
          {placeholder && <option value="" disabled>{placeholder}</option>}
          {options.map((option) => <option key={option.value} value={option.value} disabled={option.disabled}>{option.label}</option>)}
        </select>
        <ChevronDown aria-hidden="true" className="pointer-events-none absolute right-3.5 top-1/2 size-5 -translate-y-1/2 text-muted-foreground" />
      </div>
      {(error || hint) && <p id={descriptionId} className={clsx('text-xs', error ? 'text-danger' : 'text-muted-foreground')}>{error ?? hint}</p>}
    </div>
  );
});
