import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import 'bootstrap/dist/css/bootstrap.min.css';
import './custom.css';

import App from './App';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

const rootElement = document.getElementById('root');

if (!rootElement) {
  throw new Error(
    'Root element not found. Make sure there is an element with id "root" in your HTML.'
  );
}

const root = createRoot(rootElement);
root.render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  </StrictMode>
);
