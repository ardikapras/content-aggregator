import { FC } from 'react';
import { Card, Row, Col, Table, Button } from 'react-bootstrap';
import { formatDate } from '../../utils/dashboardUtils';
import { ArticleDto } from '../../services/Api.ts';

interface RecentArticlesProps {
  articles: ArticleDto[];
}

const RecentArticles: FC<RecentArticlesProps> = ({ articles }) => {
  return (
    <Row>
      <Col>
        <Card className="shadow-sm">
          <Card.Header className="d-flex justify-content-between align-items-center">
            <h5 className="card-title mb-0">Recent Articles</h5>
            <Button variant="outline-primary" size="sm" href="/articles">
              View All Articles
            </Button>
          </Card.Header>
          <Card.Body className="p-0">
            <Table responsive hover className="mb-0">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Source</th>
                  <th>Published</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {articles.map(article => (
                  <tr key={article.id}>
                    <td className="text-truncate" style={{ maxWidth: '300px' }}>
                      {article.title}
                    </td>
                    <td>{article.source}</td>
                    <td>{article.publishDate ? formatDate(article.publishDate) : 'Unknown'}</td>
                    <td>
                      <Button
                        variant="outline-secondary"
                        size="sm"
                        href={article.url}
                        target="_blank"
                      >
                        View
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </Card.Body>
        </Card>
      </Col>
    </Row>
  );
};

export default RecentArticles;
