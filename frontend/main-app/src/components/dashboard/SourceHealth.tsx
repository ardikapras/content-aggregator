import { FC } from 'react';
import { Card, Row, Col, Table, Badge, Button } from 'react-bootstrap';
import { SourceHealth as SourceHealthType } from '../../types/dashboardTypes';

interface SourceHealthProps {
  sources: SourceHealthType[];
}

const SourceHealth: FC<SourceHealthProps> = ({ sources }) => {
  return (
    <Row className="mb-4">
      <Col>
        <Card className="shadow-sm">
          <Card.Header className="d-flex justify-content-between align-items-center">
            <h5 className="card-title mb-0">Source Health</h5>
            <Button variant="outline-primary" size="sm" href="/sources">
              Manage Sources
            </Button>
          </Card.Header>
          <Card.Body className="p-0">
            <Table responsive hover className="mb-0">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Status</th>
                  <th>Last Scraped</th>
                  <th>Active</th>
                </tr>
              </thead>
              <tbody>
                {sources.slice(0, 5).map(source => (
                  <tr key={source.id}>
                    <td>{source.name}</td>
                    <td>
                      <Badge
                        bg={
                          source.status === 'healthy'
                            ? 'success'
                            : source.status === 'warning'
                              ? 'warning'
                              : 'danger'
                        }
                      >
                        {source.status}
                      </Badge>
                    </td>
                    <td>{source.lastScraped}</td>
                    <td>
                      <Badge bg={source.isActive ? 'success' : 'secondary'}>
                        {source.isActive ? 'Active' : 'Inactive'}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
            {sources.length > 5 && (
              <div className="p-3 text-center">
                <Button variant="link" href="/sources">
                  View all {sources.length} sources
                </Button>
              </div>
            )}
          </Card.Body>
        </Card>
      </Col>
    </Row>
  );
};

export default SourceHealth;
