import { useState, useEffect } from 'react';
import apiService, { ArticleDto } from '../services/Api.ts';

const ArticleList = () => {
    const [articles, setArticles] = useState<ArticleDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchArticles = async () => {
            try {
                setLoading(true);
                const data = await apiService.getArticles();
                setArticles(data);
                setError(null);
            } catch (err) {
                setError('Failed to load articles');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchArticles();
    }, []);

    const getSentimentColor = (sentiment?: number) => {
        if (!sentiment) return 'bg-gray-200';
        if (sentiment > 0.3) return 'bg-green-100 text-green-800';
        if (sentiment < -0.3) return 'bg-red-100 text-red-800';
        return 'bg-yellow-100 text-yellow-800';
    };

    if (loading) return <div className="text-center py-10">Loading articles...</div>;
    if (error) return <div className="text-center text-red-500 py-10">{error}</div>;

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold mb-6">News Articles</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {articles.map((article) => (
                    <div key={article.id} className="border rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow">
                        <div className="p-4">
                            <div className="flex justify-between items-start mb-2">
                                <span className="text-sm text-gray-600">{article.source}</span>
                                {article.sentimentLabel && (
                                    <span className={`text-xs px-2 py-1 rounded ${getSentimentColor(article.sentiment)}`}>
                    {article.sentimentLabel}
                  </span>
                                )}
                            </div>

                            <h2 className="text-xl font-semibold mb-2">{article.title}</h2>
                            <p className="text-gray-700 mb-4 line-clamp-3">{article.description}</p>

                            <div className="flex justify-between items-center text-sm text-gray-500">
                                <span>{article.author || 'Unknown author'}</span>
                                <span>{article.publishDate ? new Date(article.publishDate).toLocaleDateString() : 'Unknown date'}</span>
                            </div>

                            <a
                                href={article.url}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="mt-4 inline-block text-blue-600 hover:underline"
                            >
                                Read original article
                            </a>
                        </div>
                    </div>
                ))}
            </div>

            {articles.length === 0 && (
                <div className="text-center py-10 text-gray-500">No articles found.</div>
            )}
        </div>
    );
};

export default ArticleList;
