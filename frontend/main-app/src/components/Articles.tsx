import { FC } from 'react';
import { Container } from 'react-bootstrap';
import { useArticles, useArticleModal } from '../hooks';
import { LoadingSpinner, ErrorMessage } from './common';
import {
  SearchBar,
  ArticleDetailModal,
  ArticlePagination,
  ArticlesTable,
  PageSizeSelector,
  PaginationInfo,
} from './articles';

const Articles: FC = () => {
  const { articles, loading, error, pagination, search } = useArticles();

  const { showModal, selectedArticle, openModal, closeModal } = useArticleModal();

  if (loading) {
    return <LoadingSpinner message="Loading articles..." />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  return (
    <Container fluid className="py-4">
      {/* Header with title and page size selector */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>News Articles</h2>
        <div className="d-flex align-items-center">
          <PageSizeSelector
            pageSize={pagination.pageSize}
            onPageSizeChange={pagination.handlePageSizeChange}
          />
        </div>
      </div>

      {/* Search bar */}
      <SearchBar
        searchTerm={search.searchTerm}
        onSearchChange={search.handleSearchChange}
        onClear={search.clearSearch}
      />

      {/* Articles table */}
      <ArticlesTable articles={articles} onShowDetail={openModal} />

      {/* Pagination controls */}
      <div className="d-flex justify-content-between align-items-center">
        <PaginationInfo
          startItem={pagination.startItem}
          endItem={pagination.endItem}
          totalItems={pagination.totalArticles}
        />
        <ArticlePagination
          currentPage={pagination.currentPage}
          totalPages={pagination.totalPages}
          onPageChange={pagination.handlePageChange}
        />
      </div>

      {/* Article detail modal */}
      <ArticleDetailModal show={showModal} article={selectedArticle} onClose={closeModal} />
    </Container>
  );
};

export default Articles;
