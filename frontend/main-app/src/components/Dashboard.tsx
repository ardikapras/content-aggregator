import { FC } from 'react';
import { Alert, Button, Card, Container, Row, Col, Spinner, ButtonGroup } from 'react-bootstrap';
import { RefreshCw } from 'lucide-react';
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
    refresh,
  } = useDashboardData();

  if ((loading.stats || loading.sourceHealth || loading.activities || loading.articles) && !stats) {
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
        <Button
          onClick={() => {
            void refresh.stats();
            void refresh.sourceHealth();
            void refresh.activities();
            void refresh.articles();
            void refresh.trends();
          }}
          variant="outline-danger"
        >
          Retry
        </Button>
      </Alert>
    );
  }

  const dashboardStats = {
    totalArticles: stats?.totalArticles || 0,
    activeSources: stats?.totalActiveSources || 0,
    articlesToday: stats?.articlesLast24Hours || 0,
    lastScrapeTime: stats?.lastScrapeTime || null,
  };

  const trendData = selectedTrendView === 'scraped' ? scrapedTrend : publishedTrend;

  const handleTimeRangeChange = (range: TimeRange) => {
    setTimeRange(range);
  };

  const handleTrendViewChange = (view: 'scraped' | 'published') => {
    setTrendView(view);
  };

  return (
    <Container fluid className="dashboard">
      {/* Stats Cards */}
      <div className="mb-4">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <h5>Overview</h5>
          <Button
            variant="outline-secondary"
            size="sm"
            onClick={() => void refresh.stats()}
            disabled={loading.stats}
          >
            {loading.stats ? <Spinner animation="border" size="sm" /> : <RefreshCw size={16} />}
          </Button>
        </div>
        <StatsCards stats={dashboardStats} />
      </div>

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

            <ButtonGroup size="sm" className="me-2">
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

            <Button
              variant="outline-secondary"
              size="sm"
              onClick={() => void refresh.trends()}
              disabled={loading.trends}
            >
              {loading.trends ? <Spinner animation="border" size="sm" /> : <RefreshCw size={16} />}
            </Button>
          </div>
        </Card.Header>
        <Card.Body>
          {loading.trends ? (
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
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h5>Source Health</h5>
            <Button
              variant="outline-secondary"
              size="sm"
              onClick={() => void refresh.sourceHealth()}
              disabled={loading.sourceHealth}
            >
              {loading.sourceHealth ? (
                <Spinner animation="border" size="sm" />
              ) : (
                <RefreshCw size={16} />
              )}
            </Button>
          </div>
          <SourceHealth sources={sourceHealth} />
        </Col>
        <Col lg={4}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h5>Recent Activity</h5>
            <Button
              variant="outline-secondary"
              size="sm"
              onClick={() => void refresh.activities()}
              disabled={loading.activities}
            >
              {loading.activities ? (
                <Spinner animation="border" size="sm" />
              ) : (
                <RefreshCw size={16} />
              )}
            </Button>
          </div>
          <RecentActivity activities={recentActivities} />
        </Col>
      </Row>

      {/* Recent Articles */}
      <Row className="mb-4">
        <Col lg={12}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h5>Recent Articles</h5>
            <Button
              variant="outline-secondary"
              size="sm"
              onClick={() => void refresh.articles()}
              disabled={loading.articles}
            >
              {loading.articles ? (
                <Spinner animation="border" size="sm" />
              ) : (
                <RefreshCw size={16} />
              )}
            </Button>
          </div>
          <RecentArticles articles={recentArticles} />
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;
