import { useState, useEffect, useCallback } from 'react';
import apiService, {
  DashboardStatsDto,
  ArticleTrendDto,
  SourceHealthDto,
  RecentActivityDto,
  ArticleDto,
  TimeRange
} from '../services/Api';

interface DashboardDataHook {
  loading: boolean;
  error: string | null;
  stats: DashboardStatsDto | null;
  sourceHealth: SourceHealthDto[];
  recentActivities: RecentActivityDto[];
  recentArticles: ArticleDto[];
  scrapedTrend: ArticleTrendDto[];
  publishedTrend: ArticleTrendDto[];
  selectedTimeRange: TimeRange;
  selectedTrendView: 'scraped' | 'published';
  setTimeRange: (range: TimeRange) => void;
  setTrendView: (view: 'scraped' | 'published') => void;
  refreshData: () => Promise<void>;
  formatDate: (dateStr: string | null) => string;
}

const useDashboardData = (): DashboardDataHook => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [stats, setStats] = useState<DashboardStatsDto | null>(null);
  const [sourceHealth, setSourceHealth] = useState<SourceHealthDto[]>([]);
  const [recentActivities, setRecentActivities] = useState<RecentActivityDto[]>([]);
  const [recentArticles, setRecentArticles] = useState<ArticleDto[]>([]);
  const [scrapedTrend, setScrapedTrend] = useState<ArticleTrendDto[]>([]);
  const [publishedTrend, setPublishedTrend] = useState<ArticleTrendDto[]>([]);
  const [selectedTimeRange, setSelectedTimeRange] = useState<TimeRange>('7D');
  const [selectedTrendView, setSelectedTrendView] = useState<'scraped' | 'published'>('published');

  const formatDate = (dateStr: string | null): string => {
    if (!dateStr) return 'Never';

    try {
      const date = new Date(dateStr);
      return date.toLocaleString(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (err) {
      console.error('Date formatting error:', err);
      return 'Invalid date';
    }
  };

  const setTimeRange = useCallback((range: TimeRange) => {
    setSelectedTimeRange(range);
  }, []);

  const setTrendView = useCallback((view: 'scraped' | 'published') => {
    setSelectedTrendView(view);
  }, []);

  const fetchDashboardData = useCallback(async () => {
    setLoading(true);
    setError(null);

    const getDashboardStats = (): Promise<DashboardStatsDto> =>
      apiService.getDashboardStats();

    const getScrapedTrend = (): Promise<ArticleTrendDto[]> =>
      apiService.getArticleTrendsByScrapedDate(selectedTimeRange);

    const getPublishedTrend = (): Promise<ArticleTrendDto[]> =>
      apiService.getArticleTrendsByPublishedDate(selectedTimeRange);

    const getSourceHealth = (): Promise<SourceHealthDto[]> =>
      apiService.getSourceHealth();

    const getRecentActivities = (): Promise<RecentActivityDto[]> =>
      apiService.getRecentActivities();

    const getRecentArticles = (): Promise<ArticleDto[]> =>
      apiService.getRecentArticles();

    try {
      const statsData = await getDashboardStats();
      const scrapedTrendData = await getScrapedTrend();
      const publishedTrendData = await getPublishedTrend();
      const sourceHealthData = await getSourceHealth();
      const recentActivitiesData = await getRecentActivities();
      const recentArticlesData = await getRecentArticles();

      // Update state with the fetched data
      setStats(statsData);
      setScrapedTrend(scrapedTrendData);
      setPublishedTrend(publishedTrendData);
      setSourceHealth(sourceHealthData);
      setRecentActivities(recentActivitiesData);
      setRecentArticles(recentArticlesData);
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
      setError('Failed to load dashboard data. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [selectedTimeRange]);

  // Fetch trend data only (for time range changes)
  const fetchTrendData = useCallback(async () => {
    setLoading(true);

    try {
      const [scrapedTrendData, publishedTrendData] = await Promise.all([
        apiService.getArticleTrendsByScrapedDate(selectedTimeRange),
        apiService.getArticleTrendsByPublishedDate(selectedTimeRange)
      ]);

      setScrapedTrend(scrapedTrendData);
      setPublishedTrend(publishedTrendData);
    } catch (err) {
      console.error('Error fetching trend data:', err);
      setError('Failed to load trend data. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [selectedTimeRange]);

  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  useEffect(() => {
    fetchTrendData();
  }, [selectedTimeRange, fetchTrendData]);

  return {
    loading,
    error,
    stats,
    sourceHealth,
    recentActivities,
    recentArticles,
    scrapedTrend,
    publishedTrend,
    selectedTimeRange,
    selectedTrendView,
    setTimeRange,
    setTrendView,
    refreshData: fetchDashboardData,
    formatDate
  };
};

export default useDashboardData;
