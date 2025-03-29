import { useState } from 'react';
import apiService from '../services/Api';

/**
 * Custom hook to manage scraper operations
 */
const useScraper = (onSuccess?: () => void) => {
  const [scraperRunning, setScraperRunning] = useState(false);
  const [scraperMessage, setScraperMessage] = useState<string | null>(null);

  const triggerScraper = async () => {
    try {
      setScraperRunning(true);
      setScraperMessage('Running scraper, please wait...');

      const result = await apiService.triggerScraping();

      const totalScraped = Object.values(result).reduce((sum, count) => sum + count, 0);

      setScraperMessage(
        `Successfully scraped ${totalScraped} articles from ${Object.keys(result).length} sources.`
      );

      if (onSuccess) {
        setTimeout(onSuccess, 3000);
      }
    } catch (error) {
      console.error('Error triggering scraper:', error);
      setScraperMessage('Failed to trigger scraper. Please try again.');
    } finally {
      setScraperRunning(false);
    }
  };

  const clearScraperMessage = () => {
    setScraperMessage(null);
  };

  return {
    scraperRunning,
    scraperMessage,
    triggerScraper,
    clearScraperMessage,
  };
};

export default useScraper;
