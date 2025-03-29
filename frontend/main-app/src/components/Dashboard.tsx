import { FC } from 'react';
import { Alert, Button, Spinner, Row, Col } from 'react-bootstrap';
import {
  StatsCards,
  ArticleChart,
  RecentActivity,
  SourceHealth,
  RecentArticles,
} from './dashboard';
import { useDashboardData, useScraper, useChartData } from '../hooks';

/**
 * Dashboard component that shows an overview of the content aggregation system
 */
const Dashboard: FC = () => {
  const { stats, recentArticles, sourceHealth, recentActivity, loading, error, refreshData } =
    useDashboardData();

  const chartData = useChartData();

  const { scraperMessage, clearScraperMessage } = useScraper(() => {
    refreshData();
  });

  if (loading) {
    return (
      <div className="text-center p-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading dashboard...</span>
        </Spinner>
        <p className="mt-3">Loading dashboard...</p>
      </div>
    );
  }

  if (error) {
    return (
      <Alert variant="danger">
        <Alert.Heading>Error Loading Dashboard</Alert.Heading>
        <p>{error}</p>
        <Button onClick={refreshData} variant="outline-danger">
          Retry
        </Button>
      </Alert>
    );
  }

  return (
    <div className="dashboard">
      {scraperMessage && (
        <Alert variant="info" dismissible onClose={clearScraperMessage}>
          {scraperMessage}
        </Alert>
      )}

      {/* Stats Cards */}
      <StatsCards stats={stats} />

      {/* Charts and Activity */}
      <Row className="mb-4">
        <Col lg={8}>
          <ArticleChart chartData={chartData} />
        </Col>
        <Col lg={4}>
          <RecentActivity activities={recentActivity} />
        </Col>
      </Row>

      {/* Source Health */}
      <SourceHealth sources={sourceHealth} />

      {/* Recent Articles */}
      <RecentArticles articles={recentArticles} />
    </div>
  );
};

export default Dashboard;
