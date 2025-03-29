import { useState, useEffect, ChangeEvent, FormEvent } from 'react';
import { Table, Badge, Button, Spinner, Card, Modal, Form } from 'react-bootstrap';
import apiService, { SourceDto } from '../services/Api';

const SourceList = () => {
  const [sources, setSources] = useState<SourceDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [currentSource, setCurrentSource] = useState<SourceDto | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    url: '',
    parsingStrategy: 'DEFAULT',
    isActive: true,
  });

  const fetchSources = async () => {
    try {
      setLoading(true);
      const data = await apiService.getSources();
      setSources(data);
      setError(null);
    } catch (err) {
      setError('Failed to load sources');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSources();
  }, []);

  const handleToggleActive = async (source: SourceDto) => {
    try {
      setLoading(true);
      await apiService.toggleSourceActive(source.id);
      fetchSources();
    } catch (err) {
      setError('Failed to toggle source status');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddSource = () => {
    setCurrentSource(null);
    setFormData({
      name: '',
      url: '',
      parsingStrategy: 'DEFAULT',
      isActive: true,
    });
    setShowModal(true);
  };

  const handleEditSource = (source: SourceDto) => {
    setCurrentSource(source);
    setFormData({
      name: source.name,
      url: source.url,
      parsingStrategy: source.parsingStrategy || 'DEFAULT',
      isActive: source.isActive,
    });
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value,
    });
  };

  const handleSelectChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      if (currentSource) {
        await apiService.updateSource(currentSource.id, formData);
      } else {
        await apiService.createSource(formData);
      }
      setShowModal(false);
      fetchSources();
    } catch (err) {
      setError(`Failed to ${currentSource ? 'update' : 'create'} source`);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading sources...</span>
        </Spinner>
        <p className="mt-3">Loading sources...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-5 text-danger">
        <p>{error}</p>
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>News Sources</h2>
        <Button variant="success" onClick={handleAddSource}>
          Add New Source
        </Button>
      </div>

      {sources.length === 0 ? (
        <Card body className="text-center py-5 text-muted">
          <p>No sources found. Add a new source to get started.</p>
        </Card>
      ) : (
        <Table striped bordered hover responsive>
          <thead>
            <tr>
              <th>Name</th>
              <th>URL</th>
              <th>Last Scraped</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {sources.map(source => (
              <tr key={source.id}>
                <td>{source.name}</td>
                <td>
                  <a href={source.url} target="_blank" rel="noopener noreferrer">
                    {source.url.length > 50 ? source.url.substring(0, 50) + '...' : source.url}
                  </a>
                </td>
                <td>{source.lastScraped || 'Never'}</td>
                <td>
                  <Badge bg={source.isActive ? 'success' : 'secondary'}>
                    {source.isActive ? 'Active' : 'Inactive'}
                  </Badge>
                </td>
                <td>
                  <div className="d-flex gap-2">
                    <Button
                      variant="outline-primary"
                      size="sm"
                      onClick={() => handleEditSource(source)}
                    >
                      Edit
                    </Button>
                    <Button
                      variant={source.isActive ? 'outline-secondary' : 'outline-success'}
                      size="sm"
                      onClick={() => handleToggleActive(source)}
                    >
                      {source.isActive ? 'Deactivate' : 'Activate'}
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {/* Add/Edit Source Modal */}
      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>{currentSource ? 'Edit Source' : 'Add New Source'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>URL</Form.Label>
              <Form.Control
                type="url"
                name="url"
                value={formData.url}
                onChange={handleInputChange}
                required
              />
              <Form.Text className="text-muted">
                For RSS feeds, enter the complete URL including the XML path
              </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Parsing Strategy</Form.Label>
              <Form.Select
                name="parsingStrategy"
                value={formData.parsingStrategy}
                onChange={handleSelectChange}
              >
                <option value="DEFAULT">Default</option>
                <option value="ANTARA">Antara</option>
                <option value="CNBC">CNBC</option>
                <option value="CNN">CNN</option>
                <option value="JPNN">JPNN</option>
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                label="Active"
                name="isActive"
                checked={formData.isActive}
                onChange={handleInputChange}
              />
            </Form.Group>

            <div className="d-flex justify-content-end gap-2">
              <Button variant="secondary" onClick={handleCloseModal}>
                Cancel
              </Button>
              <Button variant="primary" type="submit">
                {currentSource ? 'Update' : 'Add'} Source
              </Button>
            </div>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default SourceList;
