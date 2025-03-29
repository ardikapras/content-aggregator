import { FC, useState, useEffect } from 'react';
import { Card, Row, Col, Table, Badge, Spinner, Alert, Tabs, Tab, Form } from 'react-bootstrap';
import { Pie, Bar } from 'react-chartjs-2';
import {
  Chart,
  ArcElement,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

Chart.register(ArcElement, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface ScraperStats {
  totalRuns: number;
  lastRun: string;
  totalArticlesScraped: number;
  successRate: number;
  averageArticlesPerRun: number;
  totalProcessingTimeMinutes: number;
}

interface SourceStats {
  sourceId: string;
  sourceName: string;
  articlesScraped: number;
  successRate: number;
  averageProcessingTimeMs: number;
  lastScrapeStatus: 'success' | 'failed';
  lastScrapeTime: string;
}

interface ArticleStatus {
  status: string;
  count: number;
  color: string;
}

interface DateRangeStats {
  date: string;
  articlesScraped: number;
  successCount: number;
  failureCount: number;
  processingTimeMinutes: number;
}

const ScraperStatistics: FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [overallStats, setOverallStats] = useState<ScraperStats | null>(null);
  const [sourceStats, setSourceStats] = useState<SourceStats[]>([]);
  const [articleStatusStats, setArticleStatusStats] = useState<ArticleStatus[]>([]);
  const [dateRangeStats, setDateRangeStats] = useState<DateRangeStats[]>([]);
  const [dateRange, setDateRange] = useState('7'); // days

  useEffect(() => {
    setTimeout(() => {
      try {
        setOverallStats({
          totalRuns: 124,
          lastRun: '2023-12-28T14:35:22',
          totalArticlesScraped: 15642,
          successRate: 94.3,
          averageArticlesPerRun: 126,
          totalProcessingTimeMinutes: 842,
        });

        setSourceStats([
          {
            sourceId: '1',
            sourceName: 'ANTARA News',
            articlesScraped: 4235,
            successRate: 96.2,
            averageProcessingTimeMs: 2540,
            lastScrapeStatus: 'success',
            lastScrapeTime: '2023-12-28T14:35:22',
          },
          {
            sourceId: '2',
            sourceName: 'CNBC Indonesia',
            articlesScraped: 3812,
            successRate: 93.7,
            averageProcessingTimeMs: 2890,
            lastScrapeStatus: 'success',
            lastScrapeTime: '2023-12-28T14:30:15',
          },
          {
            sourceId: '3',
            sourceName: 'CNN Indonesia',
            articlesScraped: 4120,
            successRate: 95.1,
            averageProcessingTimeMs: 2340,
            lastScrapeStatus: 'success',
            lastScrapeTime: '2023-12-28T14:32:45',
          },
          {
            sourceId: '4',
            sourceName: 'JPNN',
            articlesScraped: 3475,
            successRate: 91.8,
            averageProcessingTimeMs: 3120,
            lastScrapeStatus: 'failed',
            lastScrapeTime: '2023-12-28T14:28:36',
          },
        ]);

        setArticleStatusStats([
          { status: 'PROCESSED', count: 14280, color: '#198754' },
          { status: 'SCRAPED', count: 520, color: '#0d6efd' },
          { status: 'DISCOVERED', count: 420, color: '#ffc107' },
          { status: 'ERROR_SCRAPE', count: 380, color: '#dc3545' },
          { status: 'ERROR_PROCESS', count: 42, color: '#6c757d' },
        ]);

        generateDateRangeStats(parseInt(dateRange));

        setLoading(false);
      } catch (err) {
        console.error('Error loading scraper stats:', err);
        setError('Failed to load statistics data. Please try again later.');
        setLoading(false);
      }
    }, 1200);
  }, [dateRange]);

  const generateDateRangeStats = (days: number) => {
    const stats: DateRangeStats[] = [];
    const now = new Date();

    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(now);
      date.setDate(date.getDate() - i);

      const articlesScraped = Math.floor(Math.random() * 200) + 50;
      const successRate = 85 + Math.random() * 15;
      const successCount = Math.floor(articlesScraped * (successRate / 100));
      const failureCount = articlesScraped - successCount;

      stats.push({
        date: date.toISOString().split('T')[0],
        articlesScraped,
        successCount,
        failureCount,
        processingTimeMinutes: Math.floor(articlesScraped * 0.05),
      });
    }

    setDateRangeStats(stats);
  };

  const handleDateRangeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setDateRange(e.target.value);
    generateDateRangeStats(parseInt(e.target.value));
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleString();
    } catch (e) {
      console.error('Error parsing date:', e);
      return dateString;
    }
  };

  const statusChartData = {
    labels: articleStatusStats.map(s => s.status),
    datasets: [
      {
        data: articleStatusStats.map(s => s.count),
        backgroundColor: articleStatusStats.map(s => s.color),
        borderWidth: 1,
      },
    ],
  };

  const dailyActivityChartData = {
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
          <Card className="shadow-sm mb-4">
            <Card.Header className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">Daily Scraping Activity</h5>
              <Form.Select
                value={dateRange}
                onChange={handleDateRangeChange}
                style={{ width: 'auto' }}
              >
                <option value="7">Last 7 days</option>
                <option value="14">Last 14 days</option>
                <option value="30">Last 30 days</option>
              </Form.Select>
            </Card.Header>
            <Card.Body>
              <div style={{ height: '300px' }}>
                <Bar
                  data={dailyActivityChartData}
                  options={{
                    maintainAspectRatio: false,
                    scales: {
                      y: {
                        beginAtZero: true,
                      },
                    },
                  }}
                />
              </div>
            </Card.Body>
          </Card>
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
              <Card className="shadow-sm mb-4 h-100">
                <Card.Header>
                  <h5 className="mb-0">Article Status Distribution</h5>
                </Card.Header>
                <Card.Body className="d-flex justify-content-center">
                  <div style={{ height: '300px', width: '300px' }}>
                    <Pie
                      data={statusChartData}
                      options={{
                        maintainAspectRatio: false,
                        plugins: {
                          legend: {
                            position: 'right',
                          },
                        },
                      }}
                    />
                  </div>
                </Card.Body>
              </Card>
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
