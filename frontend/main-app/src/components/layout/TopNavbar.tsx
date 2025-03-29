import { FC } from 'react';
import { Link } from 'react-router-dom';
import { Button, Container, Navbar } from 'react-bootstrap';
import { NewspaperClipping } from 'phosphor-react';
import { ScraperButton } from './index.ts';

interface TopNavbarProps {
  onMenuClick: () => void;
  onRunScraper: () => void;
  isScraperRunning: boolean;
}

const TopNavbar: FC<TopNavbarProps> = ({ onMenuClick, onRunScraper, isScraperRunning }) => {
  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="mb-3">
      <Container fluid>
        <Link to="/dashboard" className="navbar-brand d-flex align-items-center">
          <NewspaperClipping size={24} className="me-2" />
          Content Aggregator
        </Link>
        <Button variant="outline-light" className="d-lg-none" onClick={onMenuClick}>
          Menu
        </Button>
        <div className="d-none d-lg-flex ms-auto">
          <ScraperButton onRunScraper={onRunScraper} isScraperRunning={isScraperRunning} />
        </div>
      </Container>
    </Navbar>
  );
};

export default TopNavbar;
