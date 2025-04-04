import { useState, useEffect, useCallback } from 'react';
import apiService, { ArticleDto } from '../services/Api';

/**
 * Custom hook to fetch and manage article data with pagination, sorting and filtering
 * Uses server-side search and filtering
 */
const useArticles = (
  sortField: string = 'publishDate',
  sortDirection: string = 'DESC',
  filterSource: string = '',
  dateRange: { from: string; to: string } = { from: '', to: '' },
  searchTerm: string = '',
  filterStatus: string = ''
) => {
  const [articles, setArticles] = useState<ArticleDto[]>([]);
  const [sources, setSources] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalArticles, setTotalArticles] = useState(0);

  const fetchArticles = useCallback(async () => {
    setLoading(true);

    try {
      const result = await apiService.getArticles(
        currentPage,
        pageSize,
        sortField,
        sortDirection,
        searchTerm,
        filterSource,
        dateRange.from,
        dateRange.to,
        filterStatus
      );

      setArticles(result.items);
      setTotalArticles(result.total);

      if (sources.length === 0) {
        const allSources = await apiService.getSources();
        const uniqueSourceNames = allSources.map(source => source.name).sort();
        setSources(uniqueSourceNames);
      }

      setError(null);
    } catch (err) {
      console.error('Error fetching articles:', err);
      setError('Failed to load articles');
    } finally {
      setLoading(false);
    }
  }, [
    currentPage,
    pageSize,
    sortField,
    sortDirection,
    searchTerm,
    filterSource,
    dateRange,
    sources.length,
    filterStatus,
  ]);

  useEffect(() => {
    const controller = new AbortController();

    fetchArticles().catch(err => {
      if (!controller.signal.aborted) {
        console.error('Error in fetchArticles:', err);
      }
    });

    return () => controller.abort();
  }, [fetchArticles]);

  const handlePageChange = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const handlePageSizeChange = (newSize: number) => {
    setPageSize(newSize);
    setCurrentPage(0);
  };

  const refresh = () => {
    fetchArticles().catch(err => console.error('Error refreshing articles:', err));
  };

  const totalPages = Math.ceil(totalArticles / pageSize);
  const startItem = totalArticles === 0 ? 0 : currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalArticles);

  return {
    articles,
    sources,
    loading,
    error,
    pagination: {
      currentPage,
      pageSize,
      totalArticles,
      totalPages,
      startItem,
      endItem,
      handlePageChange,
      handlePageSizeChange,
    },
    refresh,
  };
};

export default useArticles;
