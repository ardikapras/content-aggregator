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

export interface SourceDto {
    id: string;
    name: string;
    url: string;
    lastScraped?: string;
    isActive: boolean;
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
    getArticles: async (): Promise<ArticleDto[]> => {
        const response = await api.get<ApiResponse<ArticleDto[]>>('/articles');
        return response.data.data || [];
    },

    getArticleById: async (id: string): Promise<ArticleDto> => {
        const response = await api.get<ApiResponse<ArticleDto>>(`/articles/${id}`);
        return response.data.data as ArticleDto;
    },

    // Sources
    getSources: async (): Promise<SourceDto[]> => {
        const response = await api.get<ApiResponse<SourceDto[]>>('/sources');
        return response.data.data || [];
    },

    // Scraper operations
    triggerScraping: async (): Promise<{[source: string]: number}> => {
        const response = await api.post<ApiResponse<{[source: string]: number}>>('/scraper/run');
        return response.data.data || {};
    },

    retryPendingArticles: async (): Promise<{[source: string]: number}> => {
        const response = await api.post<ApiResponse<{[source: string]: number}>>('/scraper/re-run');
        return response.data.data || {};
    }
};

export default apiService;
