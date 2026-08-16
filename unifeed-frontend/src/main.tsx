import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import App from './App';
import { ToastProvider } from '@/components/ui';
import { AppErrorBoundary } from '@/components/app';
import { queryClient } from '@/lib/api';
import '@/i18n';
import '@/styles/globals.css';

createRoot(document.getElementById('root')!).render(<StrictMode><AppErrorBoundary><QueryClientProvider client={queryClient}><BrowserRouter><ToastProvider><App/></ToastProvider></BrowserRouter></QueryClientProvider></AppErrorBoundary></StrictMode>);
