import {useState, useEffect} from 'react';
import {Table, Form, InputGroup, Pagination, Badge, Button, Spinner, Container, Modal} from 'react-bootstrap';
import apiService, {ArticleDto} from '../services/Api';

const ArticleList = () => {
    const [articles, setArticles] = useState<ArticleDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Pagination state
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalArticles, setTotalArticles] = useState(0);

    // Search state
    const [searchTerm, setSearchTerm] = useState('');
    const [filteredArticles, setFilteredArticles] = useState<ArticleDto[]>([]);

    // Modal state
    const [showModal, setShowModal] = useState(false);
    const [selectedArticle, setSelectedArticle] = useState<ArticleDto | null>(null);

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
            const filtered = articles.filter(article =>
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

    const getSentimentVariant = (sentiment?: number) => {
        if (!sentiment) return 'secondary';
        if (sentiment > 0.3) return 'success';
        if (sentiment < -0.3) return 'danger';
        return 'warning';
    };

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
    };

    const handlePageChange = (pageNumber: number) => {
        setCurrentPage(pageNumber);
    };

    const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setPageSize(parseInt(e.target.value));
        setCurrentPage(0); // Reset to first page when changing page size
    };

    const handleShowDetail = (article: ArticleDto) => {
        setSelectedArticle(article);
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
        setSelectedArticle(null);
    };

    const formatDate = (dateString?: string) => {
        if (!dateString) return 'Unknown';
        try {
            return new Date(dateString).toLocaleDateString(undefined, {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (e) {
            return 'Invalid date';
        }
    };

    // Calculate total pages
    const totalPages = Math.ceil(totalArticles / pageSize);

    // Calculate range of items being displayed
    const startItem = currentPage * pageSize + 1;
    const endItem = Math.min((currentPage + 1) * pageSize, totalArticles);

    // Generate pagination items
    const paginationItems = [];

    // Previous button
    paginationItems.push(
        <Pagination.Prev
            key="prev"
            onClick={() => handlePageChange(Math.max(0, currentPage - 1))}
            disabled={currentPage === 0}
        />
    );

    // Page numbers
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, currentPage + 2);

    if (startPage > 0) {
        paginationItems.push(
            <Pagination.Item key={0} onClick={() => handlePageChange(0)}>1</Pagination.Item>
        );
        if (startPage > 1) {
            paginationItems.push(<Pagination.Ellipsis key="ellipsis1"/>);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        paginationItems.push(
            <Pagination.Item
                key={i}
                active={i === currentPage}
                onClick={() => handlePageChange(i)}
            >
                {i + 1}
            </Pagination.Item>
        );
    }

    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            paginationItems.push(<Pagination.Ellipsis key="ellipsis2"/>);
        }
        paginationItems.push(
            <Pagination.Item
                key={totalPages - 1}
                onClick={() => handlePageChange(totalPages - 1)}
            >
                {totalPages}
            </Pagination.Item>
        );
    }

    // Next button
    paginationItems.push(
        <Pagination.Next
            key="next"
            onClick={() => handlePageChange(Math.min(totalPages - 1, currentPage + 1))}
            disabled={currentPage >= totalPages - 1}
        />
    );

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading articles...</span>
                </Spinner>
                <p className="mt-3">Loading articles...</p>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="text-center py-5 text-danger">
                <p>{error}</p>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>News Articles</h2>

                <div className="d-flex align-items-center">
                    <Form.Group className="me-3">
                        <Form.Label>Page Size</Form.Label>
                        <Form.Select
                            value={pageSize}
                            onChange={handlePageSizeChange}
                            className="form-select-sm"
                        >
                            <option value="5">5</option>
                            <option value="10">10</option>
                            <option value="20">20</option>
                            <option value="50">50</option>
                        </Form.Select>
                    </Form.Group>
                </div>
            </div>

            <InputGroup className="mb-3">
                <Form.Control
                    placeholder="Search articles..."
                    value={searchTerm}
                    onChange={handleSearchChange}
                />
                {searchTerm && (
                    <Button
                        variant="outline-secondary"
                        onClick={() => setSearchTerm('')}
                    >
                        Clear
                    </Button>
                )}
            </InputGroup>

            {filteredArticles.length === 0 ? (
                <div className="text-center py-4 bg-light rounded">
                    <p className="mb-0">No articles found. Try a different search or add new content.</p>
                </div>
            ) : (
                <>
                    <Table striped bordered hover responsive>
                        <thead>
                        <tr>
                            <th>Title</th>
                            <th>Source</th>
                            <th>Author</th>
                            <th>Published</th>
                            <th>Sentiment</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredArticles.map((article) => (
                            <tr key={article.id}>
                                <td className="fw-bold">{article.title}</td>
                                <td>{article.source}</td>
                                <td>{article.author || 'Unknown'}</td>
                                <td>{formatDate(article.publishDate)}</td>
                                <td className="text-center">
                                    {article.sentimentLabel && (
                                        <Badge bg={getSentimentVariant(article.sentiment)}>
                                            {article.sentimentLabel}
                                        </Badge>
                                    )}
                                </td>
                                <td>
                                    <div className="d-flex gap-2">
                                        <Button
                                            variant="primary"
                                            size="sm"
                                            onClick={() => handleShowDetail(article)}
                                        >
                                            Details
                                        </Button>
                                        <Button
                                            variant="outline-secondary"
                                            size="sm"
                                            href={article.url}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                        >
                                            Original
                                        </Button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>

                    <div className="d-flex justify-content-between align-items-center">
                        <div>
                            {totalArticles > 0 ? (
                                <>Showing {startItem}-{endItem} of {totalArticles} articles</>
                            ) : (
                                <>No articles found</>
                            )}
                        </div>
                        {totalPages > 1 && <Pagination>{paginationItems}</Pagination>}
                    </div>
                </>
            )}

            {/* Article Detail Modal */}
            <Modal
                show={showModal}
                onHide={handleCloseModal}
                size="lg"
                aria-labelledby="article-detail-modal"
            >
                {selectedArticle && (
                    <>
                        <Modal.Header closeButton>
                            <Modal.Title id="article-detail-modal">
                                {selectedArticle.title}
                                {selectedArticle.sentimentLabel && (
                                    <Badge
                                        bg={getSentimentVariant(selectedArticle.sentiment)}
                                        className="ms-2"
                                    >
                                        {selectedArticle.sentimentLabel}
                                    </Badge>
                                )}
                            </Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <div className="mb-3">
                                <div className="d-flex justify-content-between mb-3">
                                    <div>
                                        <strong>Source:</strong> {selectedArticle.source}
                                    </div>
                                    <div>
                                        <strong>Published:</strong> {formatDate(selectedArticle.publishDate)}
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <strong>Author:</strong> {selectedArticle.author || 'Unknown'}
                                </div>

                                {selectedArticle.content ? (
                                    <div className="article-content">
                                        <h5>Content:</h5>
                                        <div className="bg-white border p-3 rounded"
                                             style={{maxHeight: '400px', overflow: 'auto'}}>
                                            {selectedArticle.content.split('\n\n').map((paragraph, index) => (
                                                <p key={index}>{paragraph}</p>
                                            ))}
                                        </div>
                                    </div>
                                ) : (
                                    <div className="text-center text-muted py-3">
                                        <p>No content available. View the original article for full details.</p>
                                    </div>
                                )}

                                {(selectedArticle.wordCount || selectedArticle.readingTimeMinutes) && (
                                    <div className="d-flex gap-3 mt-3 text-muted small">
                                        {selectedArticle.wordCount && (
                                            <div><strong>Word Count:</strong> {selectedArticle.wordCount}</div>
                                        )}
                                        {selectedArticle.readingTimeMinutes && (
                                            <div><strong>Reading Time:</strong> {selectedArticle.readingTimeMinutes} min
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button
                                variant="secondary"
                                onClick={handleCloseModal}
                            >
                                Close
                            </Button>
                            <Button
                                variant="primary"
                                href={selectedArticle.url}
                                target="_blank"
                                rel="noopener noreferrer"
                            >
                                Read Original
                            </Button>
                        </Modal.Footer>
                    </>
                )}
            </Modal>
        </Container>
    );
};

export default ArticleList;
