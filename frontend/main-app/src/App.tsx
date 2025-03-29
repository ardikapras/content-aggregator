import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { Alert } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import './custom.css';

import Layout from './components/Layout';
import Dashboard from './components/Dashboard.tsx';
import Articles from './components/Articles.tsx';
import SourceList from './components/SourceList';
import ParserConfigurations from './components/ParserConfigurations';
import ScraperStatistics from './components/ScraperStatistics';
import apiService from './services/Api';

const App = () => {
  const [scraperStatus, setScraperStatus] = useState<{
    loading: boolean;
    message: string | null;
    error: boolean;
  }>({
    loading: false,
    message: null,
    error: false,
  });

  const handleRunScraper = async () => {
    try {
      setScraperStatus({ loading: true, message: 'Running scraper...', error: false });

      const result = await apiService.triggerScraping();

      const totalScraped = Object.values(result).reduce((sum, count) => sum + count, 0);
      const sourceCount = Object.keys(result).length;

      setScraperStatus({
        loading: false,
        message: `Successfully scraped ${totalScraped} articles from ${sourceCount} sources.`,
        error: false,
      });

      setTimeout(() => {
        setScraperStatus(prev => ({ ...prev, message: null }));
      }, 5000);
    } catch (error) {
      console.error('Error triggering scraper:', error);
      setScraperStatus({
        loading: false,
        message: 'Failed to trigger scraper. Please try again.',
        error: true,
      });
    }
  };

  useEffect(() => {
    const handleRouteChange = () => {
      if (window.location.pathname === '/scraper/run') {
        handleRunScraper();
      }
    };

    handleRouteChange();
  }, []);

  return (
    <Router>
      {scraperStatus.message && (
        <Alert
          variant={scraperStatus.error ? 'danger' : 'info'}
          className="m-3 position-fixed top-0 end-0 shadow-sm"
          style={{ zIndex: 1050, maxWidth: '400px' }}
          dismissible
          onClose={() => setScraperStatus(prev => ({ ...prev, message: null }))}
        >
          {scraperStatus.message}
        </Alert>
      )}

      <Layout onRunScraper={handleRunScraper} isScraperRunning={scraperStatus.loading}>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/articles" element={<Articles />} />
          <Route path="/sources" element={<SourceList />} />
          <Route path="/parsers" element={<ParserConfigurations />} />
          <Route path="/stats" element={<ScraperStatistics />} />
          <Route path="/scraper/run" element={<Navigate to="/" />} /> {}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </Layout>
    </Router>
  );
};

export default App;
