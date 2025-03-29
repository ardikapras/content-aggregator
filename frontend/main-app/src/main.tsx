import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Styles
import 'bootstrap/dist/css/bootstrap.min.css';
import './custom.css';

// Main App Component
import App from './App';

// Create a client for React Query
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

// Get root element
const rootElement = document.getElementById('root');

// Check if root element exists
if (!rootElement) {
  throw new Error('Root element not found. Make sure there is an element with id "root" in your HTML.');
}

// Create root and render app
const root = createRoot(rootElement);
root.render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  </StrictMode>,
);