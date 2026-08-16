/** Shared date formatting keeps locale-specific output consistent across pages. */
export function formatDateTime(
  value: string | null | undefined,
  locale: string,
  options: Intl.DateTimeFormatOptions = { dateStyle: 'short', timeStyle: 'short' },
) {
  return value ? new Intl.DateTimeFormat(locale, options).format(new Date(value)) : '';
}
