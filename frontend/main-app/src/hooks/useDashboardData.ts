import { useState, useEffect } from 'react';
import apiService from '../services/Api';
import {
  determineSourceStatus,
  generateMockActivity,
  getLatestScrapeTime,
} from '../utils/dashboardUtils';
import { DashboardStats, Activity, SourceHealth, Article } from '../types/dashboardTypes';

/**
 * Custom hook to fetch and manage dashboard data
 */
const useDashboardData = () => {
  // State for dashboard data
  const [stats, setStats] = useState<DashboardStats>({
    totalArticles: 0,
    activeSources: 0,
    articlesToday: 0,
    lastScrapeTime: null,
  });
  const [recentArticles, setRecentArticles] = useState<Article[]>([]);
  const [sourceHealth, setSourceHealth] = useState<SourceHealth[]>([]);
  const [recentActivity, setRecentActivity] = useState<Activity[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);

      // Fetch basic stats
      const [articleResponse, sourcesResponse] = await Promise.all([
        apiService.getArticles(0, 1), // Just to get the count
        apiService.getSources(),
      ]);

      const activeSources = sourcesResponse.filter(source => source.isActive).length;
      const totalArticles = articleResponse.total;

      // Get recent articles
      const recentArticlesData = await apiService.getArticles(0, 5);

      // Generate source health data
      const sourceHealthData = sourcesResponse.map(source => ({
        id: source.id,
        name: source.name,
        isActive: source.isActive,
        lastScraped: source.lastScraped || 'Never',
        status: determineSourceStatus(source.lastScraped),
      }));

      // Mock recent activity data (would come from an actual API endpoint in production)
      const mockActivityData = generateMockActivity();

      // Update all state
      setStats({
        totalArticles,
        activeSources,
        articlesToday: Math.floor(Math.random() * 50), // Mock data
        lastScrapeTime: getLatestScrapeTime(sourceHealthData),
      });

      setRecentArticles(recentArticlesData.items);
      setSourceHealth(sourceHealthData);
      setRecentActivity(mockActivityData);
      setError(null);
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
      setError('Failed to load dashboard data. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const refreshData = () => {
    fetchDashboardData();
  };

  return {
    stats,
    recentArticles,
    sourceHealth,
    recentActivity,
    loading,
    error,
    refreshData,
  };
};

export default useDashboardData;
