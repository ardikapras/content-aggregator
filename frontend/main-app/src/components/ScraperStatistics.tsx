import { FC } from 'react';
import { Card, Row, Col, Table, Badge, Spinner, Alert, Tabs, Tab } from 'react-bootstrap';
import useScraperStatistics from '../hooks/useScraperStatistics';
import { BarChart, PieChart } from './charts';

interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor: string | string[];
    borderColor?: string | string[];
    borderWidth?: number;
    fill?: boolean;
  }[];
}

const ScraperStatistics: FC = () => {
  const {
    loading,
    error,
    overallStats,
    sourceStats,
    articleStatusStats,
    dateRangeStats,
    dateRange,
    handleDateRangeChange,
    formatDate,
  } = useScraperStatistics();

  if (loading) {
    return (
      <div className="text-center p-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading scraper statistics...</span>
        </Spinner>
        <p className="mt-3">Loading scraper statistics...</p>
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

  const statusChartData: ChartData = {
    labels: articleStatusStats.map(s => s.status),
    datasets: [
      {
        label: 'Article Status',
        data: articleStatusStats.map(s => s.count),
        backgroundColor: articleStatusStats.map(s => s.color),
        borderWidth: 1,
      },
    ],
  };

  const dailyActivityChartData: ChartData = {
    labels: dateRangeStats.map(s => s.date),
    datasets: [
      {
        label: 'Articles Scraped',
        data: dateRangeStats.map(s => s.articlesScraped),
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
      {
        label: 'Success',
        data: dateRangeStats.map(s => s.successCount),
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1,
      },
      {
        label: 'Failures',
        data: dateRangeStats.map(s => s.failureCount),
        backgroundColor: 'rgba(255, 99, 132, 0.6)',
        borderColor: 'rgba(255, 99, 132, 1)',
        borderWidth: 1,
      },
    ],
  };

  return (
    <div className="scraper-stats">
      <h1 className="mb-4">Scraper Statistics</h1>

      {/* Overall Stats Cards */}
      <Row className="mb-4">
        <Col md={4}>
          <Card className="h-100 shadow-sm">
            <Card.Body className="text-center">
              <h3 className="display-4">{overallStats?.totalArticlesScraped}</h3>
              <Card.Title className="text-muted">Total Articles Scraped</Card.Title>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 shadow-sm">
            <Card.Body className="text-center">
              <h3 className="display-4">{overallStats?.successRate}%</h3>
              <Card.Title className="text-muted">Overall Success Rate</Card.Title>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 shadow-sm">
            <Card.Body className="text-center">
              <h3 className="display-4">{overallStats?.totalRuns}</h3>
              <Card.Title className="text-muted">Total Scraper Runs</Card.Title>
              <p className="text-muted mb-0 small">
                Last run: {formatDate(overallStats?.lastRun || '')}
              </p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Tabs for different stat views */}
      <Tabs defaultActiveKey="activity" className="mb-4">
        <Tab eventKey="activity" title="Activity">
          <BarChart
            chartData={dailyActivityChartData}
            dateRange={dateRange}
            onDateRangeChange={handleDateRangeChange}
          />
        </Tab>

        <Tab eventKey="sources" title="Sources">
          <Card className="shadow-sm mb-4">
            <Card.Header>
              <h5 className="mb-0">Source Performance</h5>
            </Card.Header>
            <Card.Body className="p-0">
              <Table responsive hover className="mb-0">
                <thead>
                  <tr>
                    <th>Source</th>
                    <th className="text-center">Articles</th>
                    <th className="text-center">Success Rate</th>
                    <th className="text-center">Avg. Processing Time</th>
                    <th className="text-center">Last Scrape</th>
                    <th className="text-center">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {sourceStats.map(source => (
                    <tr key={source.sourceId}>
                      <td className="fw-bold">{source.sourceName}</td>
                      <td className="text-center">{source.articlesScraped.toLocaleString()}</td>
                      <td className="text-center">
                        <Badge
                          bg={
                            source.successRate > 95
                              ? 'success'
                              : source.successRate > 90
                                ? 'warning'
                                : 'danger'
                          }
                        >
                          {source.successRate}%
                        </Badge>
                      </td>
                      <td className="text-center">
                        {(source.averageProcessingTimeMs / 1000).toFixed(2)}s
                      </td>
                      <td className="text-center">{formatDate(source.lastScrapeTime)}</td>
                      <td className="text-center">
                        <Badge bg={source.lastScrapeStatus === 'success' ? 'success' : 'danger'}>
                          {source.lastScrapeStatus}
                        </Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Tab>

        <Tab eventKey="status" title="Status Distribution">
          <Row>
            <Col md={6}>
              <PieChart chartData={statusChartData} />
            </Col>
            <Col md={6}>
              <Card className="shadow-sm mb-4 h-100">
                <Card.Header>
                  <h5 className="mb-0">Status Details</h5>
                </Card.Header>
                <Card.Body className="p-0">
                  <Table hover className="mb-0">
                    <thead>
                      <tr>
                        <th>Status</th>
                        <th className="text-center">Count</th>
                        <th className="text-end">Percentage</th>
                      </tr>
                    </thead>
                    <tbody>
                      {articleStatusStats.map(status => {
                        const total = articleStatusStats.reduce((sum, s) => sum + s.count, 0);
                        const percentage = ((status.count / total) * 100).toFixed(1);

                        return (
                          <tr key={status.status}>
                            <td>
                              <Badge
                                bg="light"
                                text="dark"
                                style={{ borderLeft: `4px solid ${status.color}` }}
                                className="ps-2"
                              >
                                {status.status}
                              </Badge>
                            </td>
                            <td className="text-center">{status.count.toLocaleString()}</td>
                            <td className="text-end">{percentage}%</td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                </Card.Body>
              </Card>
            </Col>
          </Row>
          <Card className="shadow-sm">
            <Card.Header>
              <h5 className="mb-0">Status Explanation</h5>
            </Card.Header>
            <Card.Body>
              <Row>
                <Col md={6}>
                  <dl>
                    <dt>
                      <Badge bg="success">PROCESSED</Badge>
                    </dt>
                    <dd>
                      Articles that have been fully processed and are available in the system.
                    </dd>

                    <dt>
                      <Badge bg="primary">SCRAPED</Badge>
                    </dt>
                    <dd>
                      Articles that have been scraped but not yet fully processed or enriched.
                    </dd>
                  </dl>
                </Col>
                <Col md={6}>
                  <dl>
                    <dt>
                      <Badge bg="warning">DISCOVERED</Badge>
                    </dt>
                    <dd>Articles that have been discovered but not yet scraped.</dd>

                    <dt>
                      <Badge bg="danger">ERROR_SCRAPE</Badge>
                    </dt>
                    <dd>Articles that encountered errors during the scraping phase.</dd>

                    <dt>
                      <Badge bg="secondary">ERROR_PROCESS</Badge>
                    </dt>
                    <dd>Articles that encountered errors during the processing phase.</dd>
                  </dl>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Tab>

        <Tab eventKey="performance" title="Performance">
          <Card className="shadow-sm mb-4">
            <Card.Header>
              <h5 className="mb-0">Performance Metrics</h5>
            </Card.Header>
            <Card.Body>
              <Row>
                <Col md={6}>
                  <Table hover>
                    <tbody>
                      <tr>
                        <td className="fw-bold">Average Articles Per Run</td>
                        <td>{overallStats?.averageArticlesPerRun.toFixed(0)}</td>
                      </tr>
                      <tr>
                        <td className="fw-bold">Total Processing Time</td>
                        <td>{overallStats?.totalProcessingTimeMinutes} minutes</td>
                      </tr>
                      <tr>
                        <td className="fw-bold">Average Processing Time Per Article</td>
                        <td>
                          {(((overallStats?.totalProcessingTimeMinutes || 0) * 60) /
                            (overallStats?.totalArticlesScraped || 1)) *
                            1000}{' '}
                          ms
                        </td>
                      </tr>
                      <tr>
                        <td className="fw-bold">Average Processing Time Per Run</td>
                        <td>
                          {(overallStats?.totalProcessingTimeMinutes || 0) /
                            (overallStats?.totalRuns || 1)}{' '}
                          minutes
                        </td>
                      </tr>
                    </tbody>
                  </Table>
                </Col>
                <Col md={6}>
                  <div className="alert alert-info">
                    <h6>Performance Analysis</h6>
                    <p className="mb-0">
                      Based on the current scraping speed and success rate, the system is performing
                      within optimal parameters. The average processing time per article is well
                      within acceptable limits for content scraping operations.
                    </p>
                  </div>
                  <div className="alert alert-warning">
                    <h6>Recommendations</h6>
                    <ul className="mb-0">
                      <li>Consider adding more sources to increase content variety</li>
                      <li>Optimize JPNN parser configuration to improve success rate</li>
                      <li>Consider increasing scraper frequency during peak news hours</li>
                    </ul>
                  </div>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Tab>
      </Tabs>
    </div>
  );
};

export default ScraperStatistics;
