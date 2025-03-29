import { ChangeEvent, FC, useState } from 'react';
import { Container, Row, Col, Card, Form, Button, InputGroup } from 'react-bootstrap';
import { useArticles, useArticleModal } from '../hooks';
import { LoadingSpinner, ErrorMessage } from './common';
import { ArticleDetailModal, ArticlePagination, ArticlesTable } from './articles';
import useDebounce from '../hooks/useDebounce';

type SortField = 'publishDate' | 'title' | 'source';
type SortDirection = 'ASC' | 'DESC';

const Articles: FC = () => {
  const [sortField, setSortField] = useState<SortField>('publishDate');
  const [sortDirection, setSortDirection] = useState<SortDirection>('DESC');
  const [filterSource, setFilterSource] = useState<string>('');
  const [searchInputValue, setSearchInputValue] = useState('');
  const debouncedSearchTerm = useDebounce(searchInputValue, 500); // 500ms debounce delay
  const [dateRange, setDateRange] = useState<{ from: string; to: string }>({
    from: '',
    to: new Date().toISOString().split('T')[0],
  });

  const { articles, loading, error, pagination, sources, refresh } = useArticles(
    sortField,
    sortDirection,
    filterSource,
    dateRange,
    debouncedSearchTerm
  );

  const { showModal, selectedArticle, openModal, closeModal } = useArticleModal();

  const handlePageChange = (page: number) => {
    pagination.handlePageChange(page);
  };

  const handleSearchChange = (e: ChangeEvent<HTMLInputElement>) => {
    setSearchInputValue(e.target.value);
  };

  const handleFilterChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setFilterSource(e.target.value);
  };

  const handleDateChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setDateRange(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  if (loading && articles.length === 0) {
    return <LoadingSpinner message="Loading articles..." />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  return (
    <Container fluid className="py-4">
      <Card className="shadow-sm mb-4">
        <Card.Body>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <div>
              <h2 className="mb-0">News Articles</h2>
              <p className="text-muted">Browse and search content from all sources</p>
            </div>
            <div className="d-flex align-items-center">
              <Form.Group className="me-2">
                <Form.Label className="me-2">Page Size</Form.Label>
                <Form.Select
                  value={pagination.pageSize}
                  onChange={e => pagination.handlePageSizeChange(Number(e.target.value))}
                  style={{ width: '70px' }}
                >
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="50">50</option>
                </Form.Select>
              </Form.Group>
              <Form.Group className="me-2">
                <Form.Label className="me-2">Sort</Form.Label>
                <InputGroup>
                  <Form.Select
                    value={sortField}
                    onChange={e => setSortField(e.target.value as SortField)}
                  >
                    <option value="publishDate">Date</option>
                    <option value="title">Title</option>
                    <option value="source">Source</option>
                  </Form.Select>
                  <Button
                    variant="outline-secondary"
                    onClick={() => setSortDirection(d => (d === 'ASC' ? 'DESC' : 'ASC'))}
                  >
                    {sortDirection === 'ASC' ? '↑' : '↓'}
                  </Button>
                </InputGroup>
              </Form.Group>
              <Button variant="primary" onClick={() => refresh()}>
                Refresh
              </Button>
            </div>
          </div>

          <Row className="mb-3">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Search</Form.Label>
                <InputGroup>
                  <Form.Control
                    type="text"
                    placeholder="Search articles..."
                    value={searchInputValue}
                    onChange={handleSearchChange}
                  />
                  {searchInputValue && (
                    <Button variant="outline-secondary" onClick={() => setSearchInputValue('')}>
                      ×
                    </Button>
                  )}
                </InputGroup>
                {loading && debouncedSearchTerm && (
                  <small className="text-muted">Searching...</small>
                )}
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Source</Form.Label>
                <Form.Select value={filterSource} onChange={handleFilterChange}>
                  <option value="">All Sources</option>
                  {sources.map(source => (
                    <option key={source} value={source}>
                      {source}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group>
                <Form.Label>From Date</Form.Label>
                <Form.Control
                  type="date"
                  name="from"
                  value={dateRange.from}
                  onChange={handleDateChange}
                />
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group>
                <Form.Label>To Date</Form.Label>
                <Form.Control
                  type="date"
                  name="to"
                  value={dateRange.to}
                  onChange={handleDateChange}
                />
              </Form.Group>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Articles table */}
      <Card className="shadow-sm mb-4">
        {loading && articles.length > 0 && (
          <div
            className="position-absolute w-100 text-center py-2 bg-light bg-opacity-75"
            style={{ zIndex: 1 }}
          >
            <small>Loading...</small>
          </div>
        )}
        <Card.Body className="p-0">
          <ArticlesTable articles={articles} onShowDetail={openModal} />
        </Card.Body>

        {articles.length > 0 && (
          <Card.Footer className="bg-white d-flex justify-content-between align-items-center">
            <div>
              Showing {pagination.startItem} to {pagination.endItem} of {pagination.totalArticles}{' '}
              articles
            </div>
            <ArticlePagination
              currentPage={pagination.currentPage}
              totalPages={pagination.totalPages}
              onPageChange={handlePageChange}
            />
          </Card.Footer>
        )}
      </Card>

      {/* Article detail modal */}
      <ArticleDetailModal show={showModal} article={selectedArticle} onClose={closeModal} />
    </Container>
  );
};

export default Articles;
