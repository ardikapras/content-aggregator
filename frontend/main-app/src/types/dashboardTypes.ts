/**
 * Dashboard statistics
 */
export interface DashboardStats {
  totalArticles: number;
  activeSources: number;
  articlesToday: number;
  lastScrapeTime: string | null;
}

/**
 * Article chart data structure
 */
export interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    fill: boolean;
    backgroundColor: string;
    borderColor: string;
  }[];
}

/**
 * Activity data structure
 */
export interface Activity {
  id: string;
  timestamp: string;
  action: string;
  sourcesCount: number;
  articlesCount: number;
  status: string;
}

/**
 * Source health data structure
 */
export interface SourceHealth {
  id: string;
  name: string;
  status: 'healthy' | 'warning' | 'error';
  lastScraped: string;
  isActive: boolean;
}

/**
 * Article data structure
 */
export interface Article {
  id: string;
  title: string;
  source: string;
  publishDate: string | null;
  url: string;
  // Additional fields can be added as needed
}
