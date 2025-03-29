import { FC } from 'react';
import { Table, Badge, Button } from 'react-bootstrap';
import { ArticleDto } from '../../services/Api.ts';
import { getSentimentVariant, formatDate } from '../../utils/articleUtils.ts';
import { Eye, ArrowSquareOut } from 'phosphor-react';

interface ArticlesTableProps {
  articles: ArticleDto[];
  onShowDetail: (article: ArticleDto) => void;
}

const ArticlesTable: FC<ArticlesTableProps> = ({ articles, onShowDetail }) => {
  if (articles.length === 0) {
    return (
      <div className="text-center py-5 bg-light rounded">
        <p className="mb-0 text-muted">
          No articles found. Try adjusting your search criteria or filters.
        </p>
      </div>
    );
  }

  return (
    <Table responsive hover className="mb-0">
      <thead className="bg-light">
        <tr>
          <th style={{ width: '35%' }}>Title</th>
          <th style={{ width: '15%' }}>Source</th>
          <th style={{ width: '15%' }}>Author</th>
          <th style={{ width: '15%' }}>Published</th>
          <th style={{ width: '10%' }}>Sentiment</th>
          <th style={{ width: '10%' }} className="text-center">
            Actions
          </th>
        </tr>
      </thead>
      <tbody>
        {articles.map(article => (
          <tr key={article.id}>
            <td>
              <div className="fw-semibold text-truncate" style={{ maxWidth: '500px' }}>
                {article.title}
              </div>
              {article.description && (
                <div className="small text-muted text-truncate" style={{ maxWidth: '500px' }}>
                  {article.description}
                </div>
              )}
            </td>
            <td>
              <Badge bg="light" text="dark" className="border">
                {article.source}
              </Badge>
            </td>
            <td>{article.author || <span className="text-muted">Unknown</span>}</td>
            <td>
              {article.publishDate ? (
                <span title={article.publishDate}>{formatDate(article.publishDate)}</span>
              ) : (
                <span className="text-muted">Unknown</span>
              )}
            </td>
            <td className="text-center">
              {article.sentimentLabel ? (
                <Badge bg={getSentimentVariant(article.sentiment)} style={{ minWidth: '70px' }}>
                  {article.sentimentLabel}
                </Badge>
              ) : (
                <Badge bg="secondary" style={{ minWidth: '70px' }}>
                  N/A
                </Badge>
              )}
            </td>
            <td>
              <div className="d-flex justify-content-center gap-2">
                <Button
                  variant="outline-primary"
                  size="sm"
                  title="View Details"
                  onClick={() => onShowDetail(article)}
                >
                  <Eye size={16} />
                </Button>
                <Button
                  variant="outline-secondary"
                  size="sm"
                  title="Open Original"
                  href={article.url}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <ArrowSquareOut size={16} />
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
