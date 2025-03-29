import { ChangeEvent, FC, FormEvent } from 'react';
import { Modal, Form, Button, Row, Col, Spinner } from 'react-bootstrap';
import { ParserConfigDto, ParserFormData, ParserTestResponse } from '../../types/parserTypes';
import ParserTestResults from './ParserTestResults';

interface ParserConfigModalProps {
  show: boolean;
  onClose: () => void;
  currentParser: ParserConfigDto | null;
  formData: ParserFormData;
  onChange: (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  onSubmit: (e: FormEvent) => void;
  testUrl: string;
  setTestUrl: (url: string) => void;
  onTest: () => void;
  testLoading: boolean;
  testResult: ParserTestResponse | null;
  showTestResults: boolean;
}

const ParserConfigModal: FC<ParserConfigModalProps> = ({
  show,
  onClose,
  currentParser,
  formData,
  onChange,
  onSubmit,
  testUrl,
  setTestUrl,
  onTest,
  testLoading,
  testResult,
  showTestResults,
}) => {
  return (
    <Modal show={show} onHide={onClose} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>{currentParser ? 'Edit Parser' : 'Add New Parser'}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={onSubmit}>
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>Name</Form.Label>
                <Form.Control
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={onChange}
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
                  onChange={onChange}
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
              onChange={onChange}
              placeholder="meta[name=author]&#10;script[type=application/ld+json]&#10;.author-name"
              required
            />
            <Form.Text className="text-muted">
              Enter CSS selectors for author extraction, one per line. They will be tried in order.
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Content Selectors</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              name="contentSelectors"
              value={formData.contentSelectors}
              onChange={onChange}
              placeholder=".article-content p&#10;.content p&#10;article p"
              required
            />
            <Form.Text className="text-muted">
              Enter CSS selectors for content extraction, one per line. They will be tried in order.
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
                  onChange={onChange}
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
                  onChange={onChange}
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
                onClick={onTest}
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

          <ParserTestResults testResult={testResult} showResults={showTestResults} />
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          Cancel
        </Button>
        <Button variant="primary" onClick={onSubmit}>
          {currentParser ? 'Update' : 'Create'} Parser
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ParserConfigModal;
