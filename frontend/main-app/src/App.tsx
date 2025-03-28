import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import ArticleList from './components/ArticleList';
import { useState } from 'react';
import apiService from './services/Api.ts';

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
            <div className="min-h-screen bg-gray-50">
                <nav className="bg-white shadow">
                    <div className="container mx-auto px-4">
                        <div className="flex justify-between h-16">
                            <div className="flex items-center">
                                <Link to="/" className="text-lg font-semibold text-gray-800">
                                    Content Aggregator
                                </Link>

                                <div className="ml-10 flex items-center space-x-4">
                                    <Link to="/" className="text-gray-600 hover:text-gray-900">
                                        Articles
                                    </Link>
                                    <Link to="/sources" className="text-gray-600 hover:text-gray-900">
                                        Sources
                                    </Link>
                                </div>
                            </div>

                            <div className="flex items-center">
                                <button
                                    onClick={handleTriggerScraper}
                                    disabled={scraperStatus.loading}
                                    className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded disabled:opacity-50"
                                >
                                    {scraperStatus.loading ? 'Running Scraper...' : 'Run Scraper'}
                                </button>
                            </div>
                        </div>
                    </div>
                </nav>

                {scraperStatus.message && (
                    <div className="bg-blue-50 border-l-4 border-blue-500 p-4 my-4 mx-auto max-w-7xl">
                        <p className="text-blue-700">{scraperStatus.message}</p>
                    </div>
                )}

                <main className="container mx-auto px-4 py-8">
                    <Routes>
                        <Route path="/" element={<ArticleList />} />
                        <Route path="/sources" element={<div>Sources list will go here</div>} />
                    </Routes>
                </main>

                <footer className="bg-white border-t mt-auto py-4">
                    <div className="container mx-auto px-4 text-center text-gray-500">
                        Content Aggregator © {new Date().getFullYear()}
                    </div>
                </footer>
            </div>
        </Router>
    );
}

export default App;
