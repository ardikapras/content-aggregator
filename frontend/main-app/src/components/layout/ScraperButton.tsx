import { FC } from 'react';
import { Button, Spinner } from 'react-bootstrap';
import { Play } from 'phosphor-react';

interface ScraperButtonProps {
  onRunScraper: () => void;
  isScraperRunning: boolean;
}

export const ScraperButton: FC<ScraperButtonProps> = ({ onRunScraper, isScraperRunning }) => {
  return (
    <Button
      variant="success"
      size="sm"
      onClick={onRunScraper}
      disabled={isScraperRunning}
      className="d-flex align-items-center"
    >
      {isScraperRunning ? (
        <>
          <Spinner
            as="span"
            animation="border"
            size="sm"
            role="status"
            aria-hidden="true"
            className="me-1"
          />
          Running...
        </>
      ) : (
        <>
          <Play size={16} weight="fill" className="me-1" />
          Run Scraper
        </>
      )}
    </Button>
  );
};

export default ScraperButton;
