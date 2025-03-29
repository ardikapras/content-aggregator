import axios from 'axios';

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

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const apiService = {
  // Articles
  getArticles: async (
    page = 0,
    size = 10,
    sortBy = 'publishDate',
    direction = 'DESC'
  ): Promise<{ items: ArticleDto[]; total: number }> => {
    const response = await api.get<ApiResponse<PageResponse<ArticleDto>>>(
      `/articles?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`
    );

    if (response.data.data) {
      return {
        items: response.data.data.content || [],
        total: response.data.data.totalElements || 0,
      };
    }

    return { items: [], total: 0 };
  },

  getArticleById: async (id: string): Promise<ArticleDto> => {
    const response = await api.get<ApiResponse<ArticleDto>>(`/articles/${id}`);
    return response.data.data as ArticleDto;
  },

  getArticlesBySource: async (sourceId: string, page = 0, size = 20): Promise<ArticleDto[]> => {
    const response = await api.get<ApiResponse<ArticleDto[]>>(
      `/articles/source/${sourceId}?page=${page}&size=${size}`
    );
    return response.data.data || [];
  },

  // Sources
  getSources: async (): Promise<SourceDto[]> => {
    const response = await api.get<ApiResponse<SourceDto[]>>('/sources');
    return response.data.data || [];
  },

  getSourceById: async (id: string): Promise<SourceDto> => {
    const response = await api.get<ApiResponse<SourceDto>>(`/sources/${id}`);
    return response.data.data as SourceDto;
  },

  getActiveSources: async (): Promise<SourceDto[]> => {
    const response = await api.get<ApiResponse<SourceDto[]>>('/sources/active');
    return response.data.data || [];
  },

  createSource: async (sourceData: CreateSourceRequest): Promise<SourceDto> => {
    const response = await api.post<ApiResponse<SourceDto>>('/sources', sourceData);
    return response.data.data as SourceDto;
  },

  updateSource: async (id: string, sourceData: UpdateSourceRequest): Promise<SourceDto> => {
    const response = await api.put<ApiResponse<SourceDto>>(`/sources/${id}`, sourceData);
    return response.data.data as SourceDto;
  },

  toggleSourceActive: async (id: string): Promise<SourceDto> => {
    const response = await api.put<ApiResponse<SourceDto>>(`/sources/${id}/toggle-active`);
    return response.data.data as SourceDto;
  },

  // Scraper operations
  triggerScraping: async (): Promise<{ [source: string]: number }> => {
    const response = await api.post<ApiResponse<{ [source: string]: number }>>('/scraper/run');
    return response.data.data || {};
  },

  retryPendingArticles: async (): Promise<{ [source: string]: number }> => {
    const response = await api.post<ApiResponse<{ [source: string]: number }>>('/scraper/re-run');
    return response.data.data || {};
  },
};

export default apiService;
