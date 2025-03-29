import { FC } from 'react';
import { Modal, Badge, Button } from 'react-bootstrap';
import { ArticleDto } from '../../services/Api.ts';
import { getSentimentVariant, formatDate } from '../../utils/articleUtils.ts';

interface ArticleDetailModalProps {
  show: boolean;
  article: ArticleDto | null;
  onClose: () => void;
}

const ArticleDetailModal: FC<ArticleDetailModalProps> = ({ show, article, onClose }) => {
  if (!article) return null;

  return (
    <Modal show={show} onHide={onClose} size="lg" aria-labelledby="article-detail-modal">
      <Modal.Header closeButton>
        <Modal.Title id="article-detail-modal">
          {article.title}
          {article.sentimentLabel && (
            <Badge bg={getSentimentVariant(article.sentiment)} className="ms-2">
              {article.sentimentLabel}
            </Badge>
          )}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div className="mb-3">
          <div className="d-flex justify-content-between mb-3">
            <div>
              <strong>Source:</strong> {article.source}
            </div>
            <div>
              <strong>Published:</strong> {formatDate(article.publishDate)}
            </div>
          </div>
          <div className="mb-3">
            <strong>Author:</strong> {article.author || 'Unknown'}
          </div>

          {article.content ? (
            <div className="article-content">
              <h5>Content:</h5>
              <div
                className="bg-white border p-3 rounded"
                style={{ maxHeight: '400px', overflow: 'auto' }}
              >
                {article.content.split('\n\n').map((paragraph, index) => (
                  <p key={index}>{paragraph}</p>
                ))}
              </div>
            </div>
          ) : (
            <div className="text-center text-muted py-3">
              <p>No content available. View the original article for full details.</p>
            </div>
          )}

          {(article.wordCount || article.readingTimeMinutes) && (
            <div className="d-flex gap-3 mt-3 text-muted small">
              {article.wordCount && (
                <div>
                  <strong>Word Count:</strong> {article.wordCount}
                </div>
              )}
              {article.readingTimeMinutes && (
                <div>
                  <strong>Reading Time:</strong> {article.readingTimeMinutes} min
                </div>
              )}
            </div>
          )}
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          Close
        </Button>
        <Button variant="primary" href={article.url} target="_blank" rel="noopener noreferrer">
          Read Original
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ArticleDetailModal;
