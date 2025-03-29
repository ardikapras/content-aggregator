import { useState, useEffect } from 'react';
import apiService, { ArticleDto } from '../services/Api';

/**
 * Custom hook to fetch and manage article data with pagination
 */
const useArticles = () => {
  const [articles, setArticles] = useState<ArticleDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalArticles, setTotalArticles] = useState(0);

  const [searchTerm, setSearchTerm] = useState('');
  const [filteredArticles, setFilteredArticles] = useState<ArticleDto[]>([]);

  useEffect(() => {
    const fetchArticles = async () => {
      try {
        setLoading(true);
        const result = await apiService.getArticles(currentPage, pageSize);
        setArticles(result.items);
        setFilteredArticles(result.items);
        setTotalArticles(result.total);
        setError(null);
      } catch (err) {
        setError('Failed to load articles');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchArticles();
  }, [currentPage, pageSize]);

  // Filter articles based on search term
  useEffect(() => {
    if (searchTerm) {
      const filtered = articles.filter(
        article =>
          article.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          article.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
          (article.author && article.author.toLowerCase().includes(searchTerm.toLowerCase())) ||
          article.source.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredArticles(filtered);
    } else {
      setFilteredArticles(articles);
    }
  }, [searchTerm, articles]);

  const handlePageChange = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const handlePageSizeChange = (newSize: number) => {
    setPageSize(newSize);
    setCurrentPage(0); // Reset to first page when changing page size
  };

  const handleSearchChange = (term: string) => {
    setSearchTerm(term);
  };

  const totalPages = Math.ceil(totalArticles / pageSize);
  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalArticles);

  return {
    articles: filteredArticles,
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
    search: {
      searchTerm,
      handleSearchChange,
      clearSearch: () => setSearchTerm(''),
    },
  };
};

export default useArticles;
