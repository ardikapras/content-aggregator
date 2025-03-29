export interface ScraperStats {
  totalRuns: number;
  lastRun: string;
  totalArticlesScraped: number;
  successRate: number;
  averageArticlesPerRun: number;
  totalProcessingTimeMinutes: number;
}

export interface SourceStats {
  sourceId: string;
  sourceName: string;
  articlesScraped: number;
  successRate: number;
  averageProcessingTimeMs: number;
  lastScrapeStatus: 'success' | 'failed';
  lastScrapeTime: string;
}

export interface ArticleStatus {
  status: string;
  count: number;
  color: string;
}

export interface DateRangeStats {
  date: string;
  articlesScraped: number;
  successCount: number;
  failureCount: number;
  processingTimeMinutes: number;
}

export interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor: string | string[];
    borderColor?: string | string[];
    borderWidth?: number;
    fill?: boolean;
  }[];
}
