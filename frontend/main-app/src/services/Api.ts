import axios, { AxiosInstance, AxiosResponse } from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export interface ArticleDto {
  id: string;
  title: string;
  description: string;
  url: string;
  source: string;
  publishDate?: string;
  author?: string;
  content?: string;
  wordCount?: number;
  readingTimeMinutes?: number;
  sentiment?: number;
  sentimentLabel?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface SourceDto {
  id: string;
  name: string;
  url: string;
  parsingStrategy?: string;
  lastScraped?: string;
  isActive: boolean;
}

export interface CreateSourceRequest {
  name: string;
  url: string;
  parsingStrategy?: string;
  isActive?: boolean;
}

export interface UpdateSourceRequest {
  name?: string;
  url?: string;
  parsingStrategy?: string;
  isActive?: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  timestamp: string;
  data?: T;
  message?: string;
  error?: string;
  status: number;
}

export interface DashboardStatsDto {
  totalArticles: number;
  totalActiveSources: number;
  articlesLast24Hours: number;
  lastScrapeTime: string | null;
}

export interface ArticleTrendDto {
  date: string;
  count: number;
}

export interface SourceHealthDto {
  id: string;
  name: string;
  status: 'healthy' | 'warning' | 'error';
  lastScraped: string | null;
  isActive: boolean;
  articleCount: number;
}

export interface RecentActivityDto {
  id: string;
  timestamp: string;
  action: string;
  details: string;
}

export type TimeRange = '7D' | '1M' | '3M' | '6M' | '1Y' | 'ALL';

class ApiService {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  private extractData<T>(response: AxiosResponse<ApiResponse<T>>): T {
    return (response.data?.data as T) || ({} as T);
  }

  // Article methods
  async getArticles(
    page = 0,
    size = 10,
    sortBy = 'publishDate',
    direction = 'DESC'
  ): Promise<{ items: ArticleDto[]; total: number }> {
    const response = await this.client.get<ApiResponse<PageResponse<ArticleDto>>>(
      `/articles?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`
    );

    if (response.data?.data) {
      return {
        items: response.data.data.content || [],
        total: response.data.data.totalElements || 0,
      };
    }

    return { items: [], total: 0 };
  }

  async getArticleById(id: string): Promise<ArticleDto> {
    const response = await this.client.get<ApiResponse<ArticleDto>>(`/articles/${id}`);
    return this.extractData(response);
  }

  async getArticlesBySource(sourceId: string, page = 0, size = 20): Promise<ArticleDto[]> {
    const response = await this.client.get<ApiResponse<ArticleDto[]>>(
      `/articles/source/${sourceId}?page=${page}&size=${size}`
    );
    return response.data?.data || [];
  }

  // Source methods
  async getSources(): Promise<SourceDto[]> {
    const response = await this.client.get<ApiResponse<SourceDto[]>>('/sources');
    return response.data?.data || [];
  }

  async getSourceById(id: string): Promise<SourceDto> {
    const response = await this.client.get<ApiResponse<SourceDto>>(`/sources/${id}`);
    return this.extractData(response);
  }

  async getActiveSources(): Promise<SourceDto[]> {
    const response = await this.client.get<ApiResponse<SourceDto[]>>('/sources/active');
    return response.data?.data || [];
  }

  async createSource(sourceData: CreateSourceRequest): Promise<SourceDto> {
    const response = await this.client.post<ApiResponse<SourceDto>>('/sources', sourceData);
    return this.extractData(response);
  }

  async updateSource(id: string, sourceData: UpdateSourceRequest): Promise<SourceDto> {
    const response = await this.client.put<ApiResponse<SourceDto>>(`/sources/${id}`, sourceData);
    return this.extractData(response);
  }

  async toggleSourceActive(id: string): Promise<SourceDto> {
    const response = await this.client.put<ApiResponse<SourceDto>>(`/sources/${id}/toggle-active`);
    return this.extractData(response);
  }

  // Scraper methods
  async triggerScraping(): Promise<{ [source: string]: number }> {
    const response = await this.client.post<ApiResponse<{ [source: string]: number }>>('/scraper/run');
    return response.data?.data || {};
  }

  async retryPendingArticles(): Promise<{ [source: string]: number }> {
    const response = await this.client.post<ApiResponse<{ [source: string]: number }>>('/scraper/re-run');
    return response.data?.data || {};
  }

  // Dashboard methods
  async getDashboardStats(): Promise<DashboardStatsDto> {
    const response = await this.client.get<ApiResponse<DashboardStatsDto>>('/dashboard/stats');
    return this.extractData(response) || {
      totalArticles: 0,
      totalActiveSources: 0,
      articlesLast24Hours: 0,
      lastScrapeTime: null,
    };
  }

  async getArticleTrendsByScrapedDate(timeRange: TimeRange = '7D'): Promise<ArticleTrendDto[]> {
    const response = await this.client.get<ApiResponse<ArticleTrendDto[]>>(
      `/dashboard/trends/scraped?range=${timeRange}`
    );
    return response.data?.data || [];
  }

  async getArticleTrendsByPublishedDate(timeRange: TimeRange = '7D'): Promise<ArticleTrendDto[]> {
    const response = await this.client.get<ApiResponse<ArticleTrendDto[]>>(
      `/dashboard/trends/published?range=${timeRange}`
    );
    return response.data?.data || [];
  }

  async getSourceHealth(): Promise<SourceHealthDto[]> {
    const response = await this.client.get<ApiResponse<SourceHealthDto[]>>('/dashboard/sources/health');
    return response.data?.data || [];
  }

  async getRecentActivities(limit: number = 5): Promise<RecentActivityDto[]> {
    const response = await this.client.get<ApiResponse<RecentActivityDto[]>>(
      `/dashboard/activities?limit=${limit}`
    );
    return response.data?.data || [];
  }

  async getRecentArticles(limit: number = 5): Promise<ArticleDto[]> {
    const response = await this.client.get<ApiResponse<ArticleDto[]>>(
      `/dashboard/articles/recent?limit=${limit}`
    );
    return response.data?.data || [];
  }
}

const apiService = new ApiService();
export default apiService;
