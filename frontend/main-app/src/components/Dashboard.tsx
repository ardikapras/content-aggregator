import { FC } from 'react';
import { Alert, Button, Card, Container, Row, Col, Spinner, ButtonGroup } from 'react-bootstrap';
import useDashboardData from '../hooks/useDashboardData';
import { StatsCards, RecentActivity, SourceHealth, RecentArticles } from './dashboard';
import { ArticleTrendChart } from './charts';
import { TimeRange } from '../services/Api';

const Dashboard: FC = () => {
  const {
    loading,
    error,
    stats,
    sourceHealth,
    recentActivities,
    recentArticles,
    scrapedTrend,
    publishedTrend,
    selectedTimeRange,
    selectedTrendView,
    setTimeRange,
    setTrendView,
    refreshData,
  } = useDashboardData();

  if (loading && !stats) {
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

  const dashboardStats = {
    totalArticles: stats?.totalArticles || 0,
    activeSources: stats?.totalActiveSources || 0,
    articlesToday: stats?.articlesLast24Hours || 0,
    lastScrapeTime: stats?.lastScrapeTime || null
  };

  // Prepare trend data for chart based on selected view
  const trendData = selectedTrendView === 'scraped' ? scrapedTrend : publishedTrend;

  // Function to handle time range selection
  const handleTimeRangeChange = (range: TimeRange) => {
    setTimeRange(range);
  };

  // Function to handle trend view selection (scraped vs published)
  const handleTrendViewChange = (view: 'scraped' | 'published') => {
    setTrendView(view);
  };

  return (
    <Container fluid className="dashboard">
      {/* Stats Cards */}
      <StatsCards stats={dashboardStats} />

      {/* Article Trend Chart */}
      <Card className="shadow-sm mb-4">
        <Card.Header className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0">
            Article Trend ({selectedTrendView === 'published' ? 'Publication Date' : 'Scrape Date'})
          </h5>
          <div className="d-flex gap-2">
            <ButtonGroup size="sm" className="me-2">
              <Button
                variant={selectedTrendView === 'published' ? 'primary' : 'outline-primary'}
                onClick={() => handleTrendViewChange('published')}
              >
                Publication Date
              </Button>
              <Button
                variant={selectedTrendView === 'scraped' ? 'primary' : 'outline-primary'}
                onClick={() => handleTrendViewChange('scraped')}
              >
                Scrape Date
              </Button>
            </ButtonGroup>

            <ButtonGroup size="sm">
              <Button
                variant={selectedTimeRange === '7D' ? 'primary' : 'outline-primary'}
                onClick={() => handleTimeRangeChange('7D')}
              >
                7D
              </Button>
              <Button
                variant={selectedTimeRange === '1M' ? 'primary' : 'outline-primary'}
                onClick={() => handleTimeRangeChange('1M')}
              >
                1M
              </Button>
              <Button
                variant={selectedTimeRange === '3M' ? 'primary' : 'outline-primary'}
                onClick={() => handleTimeRangeChange('3M')}
              >
                3M
              </Button>
              <Button
                variant={selectedTimeRange === '6M' ? 'primary' : 'outline-primary'}
                onClick={() => handleTimeRangeChange('6M')}
              >
                6M
              </Button>
              <Button
                variant={selectedTimeRange === '1Y' ? 'primary' : 'outline-primary'}
                onClick={() => handleTimeRangeChange('1Y')}
              >
                1Y
              </Button>
              <Button
                variant={selectedTimeRange === 'ALL' ? 'primary' : 'outline-primary'}
                onClick={() => handleTimeRangeChange('ALL')}
              >
                ALL
              </Button>
            </ButtonGroup>
          </div>
        </Card.Header>
        <Card.Body>
          {loading ? (
            <div className="text-center py-5">
              <Spinner animation="border" size="sm" />
              <p>Loading trend data...</p>
            </div>
          ) : (
            <ArticleTrendChart trendData={trendData} />
          )}
        </Card.Body>
      </Card>

      {/* Dashboard Content */}
      <Row className="mb-4">
        <Col lg={8}>
          <SourceHealth sources={sourceHealth} />
        </Col>
        <Col lg={4}>
          <RecentActivity activities={recentActivities} />
        </Col>
      </Row>

      {/* Recent Articles */}
      <RecentArticles articles={recentArticles} />
    </Container>
  );
};

export default Dashboard;
