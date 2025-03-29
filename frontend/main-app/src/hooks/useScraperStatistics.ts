import { useState, useEffect, useCallback } from 'react';
import { ScraperStats, SourceStats, ArticleStatus, DateRangeStats } from '../types/statisticsTypes';

export const useScraperStatistics = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [overallStats, setOverallStats] = useState<ScraperStats | null>(null);
  const [sourceStats, setSourceStats] = useState<SourceStats[]>([]);
  const [articleStatusStats, setArticleStatusStats] = useState<ArticleStatus[]>([]);
  const [dateRangeStats, setDateRangeStats] = useState<DateRangeStats[]>([]);
  const [dateRange, setDateRange] = useState('7'); // days

  const generateDateRangeStats = useCallback((days: number) => {
    const stats: DateRangeStats[] = [];
    const now = new Date();

    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(now);
      date.setDate(date.getDate() - i);

      const articlesScraped = Math.floor(Math.random() * 200) + 50;
      const successRate = 85 + Math.random() * 15;
      const successCount = Math.floor(articlesScraped * (successRate / 100));
      const failureCount = articlesScraped - successCount;

      stats.push({
        date: date.toISOString().split('T')[0],
        articlesScraped,
        successCount,
        failureCount,
        processingTimeMinutes: Math.floor(articlesScraped * 0.05),
      });
    }

    setDateRangeStats(stats);
  }, []);

  const fetchStatisticsData = useCallback(async () => {
    try {
      setLoading(true);
      await new Promise(resolve => setTimeout(resolve, 1200));

      setOverallStats({
        totalRuns: 124,
        lastRun: '2023-12-28T14:35:22',
        totalArticlesScraped: 15642,
        successRate: 94.3,
        averageArticlesPerRun: 126,
        totalProcessingTimeMinutes: 842,
      });

      setSourceStats([
        {
          sourceId: '1',
          sourceName: 'ANTARA News',
          articlesScraped: 4235,
          successRate: 96.2,
          averageProcessingTimeMs: 2540,
          lastScrapeStatus: 'success',
          lastScrapeTime: '2023-12-28T14:35:22',
        },
        {
          sourceId: '2',
          sourceName: 'CNBC Indonesia',
          articlesScraped: 3812,
          successRate: 93.7,
          averageProcessingTimeMs: 2890,
          lastScrapeStatus: 'success',
          lastScrapeTime: '2023-12-28T14:30:15',
        },
        {
          sourceId: '3',
          sourceName: 'CNN Indonesia',
          articlesScraped: 4120,
          successRate: 95.1,
          averageProcessingTimeMs: 2340,
          lastScrapeStatus: 'success',
          lastScrapeTime: '2023-12-28T14:32:45',
        },
        {
          sourceId: '4',
          sourceName: 'JPNN',
          articlesScraped: 3475,
          successRate: 91.8,
          averageProcessingTimeMs: 3120,
          lastScrapeStatus: 'failed',
          lastScrapeTime: '2023-12-28T14:28:36',
        },
      ]);

      setArticleStatusStats([
        { status: 'PROCESSED', count: 14280, color: '#198754' },
        { status: 'SCRAPED', count: 520, color: '#0d6efd' },
        { status: 'DISCOVERED', count: 420, color: '#ffc107' },
        { status: 'ERROR_SCRAPE', count: 380, color: '#dc3545' },
        { status: 'ERROR_PROCESS', count: 42, color: '#6c757d' },
      ]);

      generateDateRangeStats(parseInt(dateRange));
      setError(null);
      setLoading(false);
    } catch (err) {
      console.error('Error loading scraper statistics:', err);
      setError('Failed to load statistics data. Please try again later.');
      setLoading(false);
    }
  }, [dateRange, generateDateRangeStats]);

  useEffect(() => {
    (function () {
      fetchStatisticsData().catch(err => {
        console.error('Error in fetchStatisticsData:', err);
      });
    })();
  }, [fetchStatisticsData]);

  useEffect(() => {
    generateDateRangeStats(parseInt(dateRange));
  }, [dateRange, generateDateRangeStats]);

  const handleDateRangeChange = (value: string) => {
    setDateRange(value);
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleString();
    } catch (e) {
      console.error('Error parsing date:', e);
      return dateString;
    }
  };

  return {
    loading,
    error,
    overallStats,
    sourceStats,
    articleStatusStats,
    dateRangeStats,
    dateRange,
    handleDateRangeChange,
    formatDate,
  };
};

export default useScraperStatistics;
