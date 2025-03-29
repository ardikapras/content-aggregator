import { FC, useState, useEffect } from 'react';
import { Card, Table, Button, Modal, Form, Row, Col, Badge, Spinner, Alert } from 'react-bootstrap';

interface ParserConfig {
  id: string;
  name: string;
  description: string;
  authorSelectors: string[];
  contentSelectors: string[];
  nextPageSelector?: string;
  contentFilters: string[];
  createdAt: string;
  updatedAt: string;
}

interface TestResult {
  author?: string;
  contentPreview?: string;
  success: boolean;
  message?: string;
}

const ParserConfigurations: FC = () => {
  const [parsers, setParsers] = useState<ParserConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [error] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editingConfig, setEditingConfig] = useState<ParserConfig | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    authorSelectors: '',
    contentSelectors: '',
    nextPageSelector: '',
    contentFilters: '',
  });
  const [testUrl, setTestUrl] = useState('');
  const [testLoading, setTestLoading] = useState(false);
  const [testResult, setTestResult] = useState<TestResult | null>(null);
  const [showTestResults, setShowTestResults] = useState(false);

  useEffect(() => {
    setTimeout(() => {
      setParsers([
        {
          id: '1',
          name: 'ANTARA',
          description: 'Parser for Antara News',
          authorSelectors: ['script[type=application/ld+json]'],
          contentSelectors: ['div.post-content'],
          contentFilters: ['span.baca-juga', 'p.text-muted'],
          createdAt: '2023-06-15T10:30:00',
          updatedAt: '2023-12-20T14:45:00',
        },
        {
          id: '2',
          name: 'CNBC',
          description: 'Parser for CNBC Indonesia',
          authorSelectors: ['script[type=application/ld+json]'],
          contentSelectors: ['.detail-text', 'article p', '.article-content p'],
          contentFilters: ['.media-institusi'],
          createdAt: '2023-06-15T10:30:00',
          updatedAt: '2023-12-21T09:15:00',
        },
        {
          id: '3',
          name: 'CNN',
          description: 'Parser for CNN Indonesia',
          authorSelectors: ['meta[name=author]', 'meta[name=content_author]'],
          contentSelectors: ['.detail-text p', '.detail-wrap p', '.content-artikel p'],
          contentFilters: ['.para_caption'],
          createdAt: '2023-06-16T08:45:00',
          updatedAt: '2023-12-19T11:30:00',
        },
        {
          id: '4',
          name: 'DEFAULT',
          description: 'Default parser configuration',
          authorSelectors: ['script[type=application/ld+json]', 'meta[name=author]'],
          contentSelectors: ['.content p', 'main p', 'article p'],
          contentFilters: [],
          createdAt: '2023-06-14T09:00:00',
          updatedAt: '2023-12-18T16:20:00',
        },
      ]);
      setLoading(false);
    }, 1000);
  }, []);

  const handleShowModal = (config: ParserConfig | null = null) => {
    if (config) {
      setEditingConfig(config);
      setFormData({
        name: config.name,
        description: config.description || '',
        authorSelectors: config.authorSelectors.join('\n'),
        contentSelectors: config.contentSelectors.join('\n'),
        nextPageSelector: config.nextPageSelector || '',
        contentFilters: config.contentFilters.join('\n'),
      });
    } else {
      // New config
      setEditingConfig(null);
      setFormData({
        name: '',
        description: '',
        authorSelectors: '',
        contentSelectors: '',
        nextPageSelector: '',
        contentFilters: '',
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setTestResult(null);
    setShowTestResults(false);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const newConfig: Partial<ParserConfig> = {
      name: formData.name,
      description: formData.description,
      authorSelectors: formData.authorSelectors.split('\n').filter(s => s.trim() !== ''),
      contentSelectors: formData.contentSelectors.split('\n').filter(s => s.trim() !== ''),
      nextPageSelector: formData.nextPageSelector || undefined,
      contentFilters: formData.contentFilters.split('\n').filter(s => s.trim() !== ''),
    };

    console.log('Saving parser config:', newConfig);

    if (editingConfig) {
      setParsers(prevParsers =>
        prevParsers.map(p =>
          p.id === editingConfig.id
            ? { ...p, ...newConfig, updatedAt: new Date().toISOString() }
            : p
        )
      );
    } else {
      const newId = Math.max(...parsers.map(p => parseInt(p.id))) + 1;
      setParsers(prevParsers => [
        ...prevParsers,
        {
          id: newId.toString(),
          ...(newConfig as ParserConfig),
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        },
      ]);
    }

    handleCloseModal();
  };

  const handleTestConfig = () => {
    if (!testUrl) {
      alert('Please enter a URL to test');
      return;
    }

    setTestLoading(true);
    setTestResult(null);

    setTimeout(() => {
      setTestResult({
        author: 'John Doe',
        contentPreview:
          'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam euismod, nisl eget ultricies ultricies, nunc nisl ultricies nunc, quis ultricies nisl nisl eget ultricies ultricies. Nullam euismod, nisl eget ultricies ultricies, nunc nisl ultricies nunc, quis ultricies nisl nisl eget ultricies ultricies.',
        success: true,
        message: 'Parser test successful',
      });
      setTestLoading(false);
      setShowTestResults(true);
    }, 2000);
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    } catch (e) {
      console.error(e);
      return dateString;
    }
  };

  if (loading) {
    return (
      <div className="text-center p-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading parser configurations...</span>
        </Spinner>
        <p className="mt-3">Loading parser configurations...</p>
      </div>
    );
  }

  if (error) {
    return (
      <Alert variant="danger">
        <Alert.Heading>Error</Alert.Heading>
        <p>{error}</p>
      </Alert>
    );
  }

  return (
    <div className="parser-configs">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Parser Configurations</h1>
        <Button variant="primary" onClick={() => handleShowModal()}>
          Add New Parser
        </Button>
      </div>

      <Card className="shadow-sm">
        <Card.Body className="p-0">
          <Table responsive hover className="mb-0">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Selectors</th>
                <th>Last Updated</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {parsers.map(parser => (
                <tr key={parser.id}>
                  <td className="fw-bold">{parser.name}</td>
                  <td>{parser.description}</td>
                  <td>
                    <Badge bg="info" className="me-1">
                      {parser.authorSelectors.length} author
                    </Badge>
                    <Badge bg="success" className="me-1">
                      {parser.contentSelectors.length} content
                    </Badge>
                    {parser.contentFilters.length > 0 && (
                      <Badge bg="warning">{parser.contentFilters.length} filters</Badge>
                    )}
                  </td>
                  <td>{formatDate(parser.updatedAt)}</td>
                  <td>
                    <Button
                      variant="outline-secondary"
                      size="sm"
                      className="me-2"
                      onClick={() => handleShowModal(parser)}
                    >
                      Edit
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => {
                        if (window.confirm(`Are you sure you want to delete ${parser.name}?`)) {
                          // In a real app, this would call an API to delete
                          setParsers(prev => prev.filter(p => p.id !== parser.id));
                        }
                      }}
                    >
                      Delete
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Card.Body>
      </Card>

      {/* Parser Configuration Modal */}
      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingConfig ? `Edit Parser: ${editingConfig.name}` : 'Create New Parser'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Name</Form.Label>
                  <Form.Control
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    required
                  />
                  <Form.Text className="text-muted">
                    A unique name for this parser configuration
                  </Form.Text>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Description</Form.Label>
                  <Form.Control
                    type="text"
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Author Selectors</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="authorSelectors"
                value={formData.authorSelectors}
                onChange={handleInputChange}
                placeholder="meta[name=author]&#10;script[type=application/ld+json]&#10;.author-name"
                required
              />
              <Form.Text className="text-muted">
                Enter CSS selectors for author extraction, one per line. They will be tried in
                order.
              </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Content Selectors</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="contentSelectors"
                value={formData.contentSelectors}
                onChange={handleInputChange}
                placeholder=".article-content p&#10;.content p&#10;article p"
                required
              />
              <Form.Text className="text-muted">
                Enter CSS selectors for content extraction, one per line. They will be tried in
                order.
              </Form.Text>
            </Form.Group>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Next Page Selector</Form.Label>
                  <Form.Control
                    type="text"
                    name="nextPageSelector"
                    value={formData.nextPageSelector}
                    onChange={handleInputChange}
                    placeholder=".pagination .next a"
                  />
                  <Form.Text className="text-muted">
                    CSS selector for next page link (for multi-page articles)
                  </Form.Text>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Content Filters</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="contentFilters"
                    value={formData.contentFilters}
                    onChange={handleInputChange}
                    placeholder="span.advertisement&#10;.baca-juga&#10;^Related:.*"
                  />
                  <Form.Text className="text-muted">
                    Enter regex patterns to filter out unwanted content, one per line
                  </Form.Text>
                </Form.Group>
              </Col>
            </Row>

            <hr />

            <h5>Test Parser</h5>
            <Row className="align-items-end">
              <Col md={8}>
                <Form.Group className="mb-3">
                  <Form.Label>Test URL</Form.Label>
                  <Form.Control
                    type="url"
                    value={testUrl}
                    onChange={e => setTestUrl(e.target.value)}
                    placeholder="https://example.com/article"
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Button
                  variant="secondary"
                  className="mb-3 w-100"
                  onClick={handleTestConfig}
                  disabled={testLoading}
                >
                  {testLoading ? (
                    <>
                      <Spinner
                        as="span"
                        animation="border"
                        size="sm"
                        role="status"
                        aria-hidden="true"
                      />
                      <span className="ms-2">Testing...</span>
                    </>
                  ) : (
                    'Test Parser'
                  )}
                </Button>
              </Col>
            </Row>

            {showTestResults && testResult && (
              <Alert variant={testResult.success ? 'success' : 'danger'}>
                <Alert.Heading>
                  {testResult.success ? 'Parser Test Successful' : 'Parser Test Failed'}
                </Alert.Heading>
                {testResult.message && <p>{testResult.message}</p>}

                {testResult.success && (
                  <>
                    <h6>Extracted Author:</h6>
                    <p className="mb-3">{testResult.author || 'None'}</p>

                    <h6>Content Preview:</h6>
                    <div
                      className="bg-light p-3 rounded"
                      style={{ maxHeight: '200px', overflow: 'auto' }}
                    >
                      {testResult.contentPreview}
                    </div>
                  </>
                )}
              </Alert>
            )}
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Cancel
          </Button>
          <Button variant="primary" onClick={handleSubmit}>
            {editingConfig ? 'Update' : 'Create'} Parser
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ParserConfigurations;
