import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  define: { global: 'globalThis' },
  resolve: { alias: { '@': new URL('./src', import.meta.url).pathname } },
  server: { port: 5173, proxy: { '/api': 'http://localhost:8080', '/uploads': 'http://localhost:8080', '/ws': { target: 'http://localhost:8080', ws: true } } },
  test: { environment: 'jsdom', setupFiles: ['./src/test/setup.ts'], css: true, exclude: ['e2e/**', 'node_modules/**'] }
});
