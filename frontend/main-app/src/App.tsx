import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { useState } from 'react';
import { Container, Navbar, Nav, Button, Alert, Spinner } from 'react-bootstrap';
import ArticleList from './components/ArticleList';
import SourceList from './components/SourceList';
import apiService from './services/Api';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
    const [scraperStatus, setScraperStatus] = useState<{
        loading: boolean;
        message: string | null;
    }>({
        loading: false,
        message: null,
    });

    const handleTriggerScraper = async () => {
        try {
            setScraperStatus({ loading: true, message: null });
            const result = await apiService.triggerScraping();

            // Calculate total articles scraped
            const totalScraped = Object.values(result).reduce((sum, count) => sum + count, 0);

            setScraperStatus({
                loading: false,
                message: `Successfully scraped ${totalScraped} articles from ${Object.keys(result).length} sources.`
            });
        } catch (error) {
            setScraperStatus({
                loading: false,
                message: 'Failed to trigger scraper. Please try again.'
            });
        }
    };

    return (
        <Router>
            <div className="d-flex flex-column min-vh-100">
                <Navbar bg="dark" variant="dark" expand="lg">
                    <Container>
                        <Navbar.Brand as={Link} to="/">Content Aggregator</Navbar.Brand>
                        <Navbar.Toggle aria-controls="basic-navbar-nav" />
                        <Navbar.Collapse id="basic-navbar-nav">
                            <Nav className="me-auto">
                                <Nav.Link as={Link} to="/">Articles</Nav.Link>
                                <Nav.Link as={Link} to="/sources">Sources</Nav.Link>
                            </Nav>
                            <Button
                                variant="primary"
                                onClick={handleTriggerScraper}
                                disabled={scraperStatus.loading}
                            >
                                {scraperStatus.loading ? (
                                    <>
                                        <Spinner
                                            as="span"
                                            animation="border"
                                            size="sm"
                                            role="status"
                                            aria-hidden="true"
                                        />
                                        <span className="ms-2">Running Scraper...</span>
                                    </>
                                ) : 'Run Scraper'}
                            </Button>
                        </Navbar.Collapse>
                    </Container>
                </Navbar>

                {scraperStatus.message && (
                    <Container className="mt-3">
                        <Alert variant="info">
                            {scraperStatus.message}
                        </Alert>
                    </Container>
                )}

                <Container className="py-4 flex-grow-1">
                    <Routes>
                        <Route path="/" element={<ArticleList />} />
                        <Route path="/sources" element={<SourceList />} />
                    </Routes>
                </Container>

                <footer className="py-3 bg-dark text-white mt-auto">
                    <Container className="text-center">
                        <p className="mb-0">Content Aggregator © {new Date().getFullYear()}</p>
                    </Container>
                </footer>
            </div>
        </Router>
    );
}

export default App;
