import { FC } from 'react';
import { Card, Row, Col } from 'react-bootstrap';
import { DashboardStats } from '../../types/dashboardTypes';
import { formatDate } from '../../utils/dashboardUtils';

interface StatsCardsProps {
  stats: DashboardStats;
}

const StatsCards: FC<StatsCardsProps> = ({ stats }) => {
  return (
    <Row className="mb-4">
      <Col md={3}>
        <Card className="h-100 shadow-sm">
          <Card.Body className="text-center">
            <h3 className="display-4">{stats.totalArticles}</h3>
            <Card.Title className="text-muted">Total Articles</Card.Title>
          </Card.Body>
        </Card>
      </Col>
      <Col md={3}>
        <Card className="h-100 shadow-sm">
          <Card.Body className="text-center">
            <h3 className="display-4">{stats.activeSources}</h3>
            <Card.Title className="text-muted">Active Sources</Card.Title>
          </Card.Body>
        </Card>
      </Col>
      <Col md={3}>
        <Card className="h-100 shadow-sm">
          <Card.Body className="text-center">
            <h3 className="display-4">{stats.articlesToday}</h3>
            <Card.Title className="text-muted">Articles Scraped Today</Card.Title>
          </Card.Body>
        </Card>
      </Col>
      <Col md={3}>
        <Card className="h-100 shadow-sm">
          <Card.Body className="text-center">
            <p className="mb-1 text-muted small">Last Scrape</p>
            <h5>
              {stats.lastScrapeTime === 'Never' ? 'Never' : formatDate(stats.lastScrapeTime || '')}
            </h5>
          </Card.Body>
        </Card>
      </Col>
    </Row>
  );
};

export default StatsCards;
