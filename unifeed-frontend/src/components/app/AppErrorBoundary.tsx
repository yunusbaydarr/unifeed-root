import { Component, type ErrorInfo, type ReactNode } from 'react';
import { AlertTriangle } from 'lucide-react';
import { Button, EmptyState } from '@/components/ui';
import i18n from '@/i18n';

interface State { failed: boolean }

export class AppErrorBoundary extends Component<{ children: ReactNode }, State> {
  state: State = { failed: false };
  static getDerivedStateFromError(): State { return { failed: true }; }
  componentDidCatch(_error: Error, _info: ErrorInfo) { /* Reporting provider is intentionally deployment-owned. */ }
  render() {
    if (!this.state.failed) return this.props.children;
    return <main className="grid min-h-dvh place-items-center bg-background p-6"><EmptyState icon={<AlertTriangle/>} title={i18n.t('errorBoundary.title')} description={i18n.t('errorBoundary.description')} action={<Button onClick={()=>window.location.reload()}>{i18n.t('errorBoundary.reload')}</Button>}/></main>;
  }
}
