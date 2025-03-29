import { useState, useEffect, useCallback } from 'react';
import apiService, {
  DashboardStatsDto,
  ArticleTrendDto,
  SourceHealthDto,
  RecentActivityDto,
  ArticleDto,
  TimeRange,
} from '../services/Api';

interface DashboardDataHook {
  loading: {
    stats: boolean;
    sourceHealth: boolean;
    activities: boolean;
    articles: boolean;
    trends: boolean;
  };
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
  refresh: {
    stats: () => Promise<void>;
    sourceHealth: () => Promise<void>;
    activities: () => Promise<void>;
    articles: () => Promise<void>;
    trends: () => Promise<void>;
  };
  formatDate: (dateStr: string | null) => string;
}

const useDashboardData = (): DashboardDataHook => {
  const [loading, setLoading] = useState({
    stats: true,
    sourceHealth: true,
    activities: true,
    articles: true,
    trends: true,
  });
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
        minute: '2-digit',
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

  const refreshStats = useCallback(async () => {
    setLoading(prev => ({ ...prev, stats: true }));
    setError(null);

    try {
      const statsData = await apiService.getDashboardStats();
      setStats(statsData);
    } catch (err) {
      console.error('Error fetching dashboard stats:', err);
      setError('Failed to load dashboard stats. Please try again.');
    } finally {
      setLoading(prev => ({ ...prev, stats: false }));
    }
  }, []);

  const refreshSourceHealth = useCallback(async () => {
    setLoading(prev => ({ ...prev, sourceHealth: true }));
    setError(null);

    try {
      const sourceHealthData = await apiService.getSourceHealth();
      setSourceHealth(sourceHealthData);
    } catch (err) {
      console.error('Error fetching source health:', err);
      setError('Failed to load source health data. Please try again.');
    } finally {
      setLoading(prev => ({ ...prev, sourceHealth: false }));
    }
  }, []);

  const refreshActivities = useCallback(async () => {
    setLoading(prev => ({ ...prev, activities: true }));
    setError(null);

    try {
      const recentActivitiesData = await apiService.getRecentActivities();
      setRecentActivities(recentActivitiesData);
    } catch (err) {
      console.error('Error fetching recent activities:', err);
      setError('Failed to load recent activities. Please try again.');
    } finally {
      setLoading(prev => ({ ...prev, activities: false }));
    }
  }, []);

  const refreshArticles = useCallback(async () => {
    setLoading(prev => ({ ...prev, articles: true }));
    setError(null);

    try {
      const recentArticlesData = await apiService.getRecentArticles();
      setRecentArticles(recentArticlesData);
    } catch (err) {
      console.error('Error fetching recent articles:', err);
      setError('Failed to load recent articles. Please try again.');
    } finally {
      setLoading(prev => ({ ...prev, articles: false }));
    }
  }, []);

  const refreshTrends = useCallback(async () => {
    setLoading(prev => ({ ...prev, trends: true }));
    setError(null);

    try {
      const [scrapedTrendData, publishedTrendData] = await Promise.all([
        apiService.getArticleTrendsByScrapedDate(selectedTimeRange),
        apiService.getArticleTrendsByPublishedDate(selectedTimeRange),
      ]);

      setScrapedTrend(scrapedTrendData);
      setPublishedTrend(publishedTrendData);
    } catch (err) {
      console.error('Error fetching trend data:', err);
      setError('Failed to load trend data. Please try again.');
    } finally {
      setLoading(prev => ({ ...prev, trends: false }));
    }
  }, [selectedTimeRange]);

  useEffect(() => {
    void refreshStats();
    void refreshSourceHealth();
    void refreshActivities();
    void refreshArticles();
  }, [refreshStats, refreshSourceHealth, refreshActivities, refreshArticles]);

  useEffect(() => {
    void refreshTrends();
  }, [selectedTimeRange, refreshTrends]);

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
    refresh: {
      stats: refreshStats,
      sourceHealth: refreshSourceHealth,
      activities: refreshActivities,
      articles: refreshArticles,
      trends: refreshTrends,
    },
    formatDate,
  };
};

export default useDashboardData;
