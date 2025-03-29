import { FC } from 'react';
import { Table, Badge, Button } from 'react-bootstrap';
import { ArticleDto } from '../../services/Api.ts';
import { getSentimentVariant, formatDate } from '../../utils/articleUtils.ts';

interface ArticlesTableProps {
  articles: ArticleDto[];
  onShowDetail: (article: ArticleDto) => void;
}

const ArticlesTable: FC<ArticlesTableProps> = ({ articles, onShowDetail }) => {
  if (articles.length === 0) {
    return (
      <div className="text-center py-4 bg-light rounded">
        <p className="mb-0">No articles found. Try a different search or add new content.</p>
      </div>
    );
  }

  return (
    <Table striped bordered hover responsive>
      <thead>
        <tr>
          <th>Title</th>
          <th>Source</th>
          <th>Author</th>
          <th>Published</th>
          <th>Sentiment</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {articles.map(article => (
          <tr key={article.id}>
            <td className="fw-bold">{article.title}</td>
            <td>{article.source}</td>
            <td>{article.author || 'Unknown'}</td>
            <td>{formatDate(article.publishDate)}</td>
            <td className="text-center">
              {article.sentimentLabel && (
                <Badge bg={getSentimentVariant(article.sentiment)}>{article.sentimentLabel}</Badge>
              )}
            </td>
            <td>
              <div className="d-flex gap-2">
                <Button variant="primary" size="sm" onClick={() => onShowDetail(article)}>
                  Details
                </Button>
                <Button
                  variant="outline-secondary"
                  size="sm"
                  href={article.url}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Original
                </Button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
};

export default ArticlesTable;
