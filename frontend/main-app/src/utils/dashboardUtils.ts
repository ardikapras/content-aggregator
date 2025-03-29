/**
 * Format date for display
 */
export const formatDate = (dateString: string) => {
  try {
    const date = new Date(dateString);
    return date.toLocaleString();
  } catch (e) {
    console.error('Error formatting date:', e);
    return dateString;
  }
};

/**
 * Determine source health status based on last scraped time
 */
export const determineSourceStatus = (
  lastScraped: string | null
): 'healthy' | 'warning' | 'error' => {
  if (!lastScraped || lastScraped === 'Never') return 'error';

  const lastScrapedDate = new Date(lastScraped);
  const now = new Date();
  const hoursSinceLastScrape = (now.getTime() - lastScrapedDate.getTime()) / (1000 * 60 * 60);

  if (hoursSinceLastScrape < 24) return 'healthy';
  if (hoursSinceLastScrape < 48) return 'warning';
  return 'error';
};

/**
 * Generate mock activity data for the dashboard
 */
export const generateMockActivity = () => {
  const activities = [];
  const now = new Date();

  for (let i = 0; i < 5; i++) {
    const timeAgo = new Date(now.getTime() - i * 3600000 * Math.random() * 5);
    activities.push({
      id: `act-${i}`,
      timestamp: timeAgo.toISOString(),
      action: 'Scraper Run',
      sourcesCount: Math.floor(Math.random() * 10) + 5,
      articlesCount: Math.floor(Math.random() * 100),
      status: Math.random() > 0.2 ? 'success' : 'failed',
    });
  }

  return activities;
};

/**
 * Gets the most recent scrape time from a list of sources
 */
export const getLatestScrapeTime = (sources: Array<{ lastScraped: string | null }>) => {
  return sources.reduce((latest, src) => {
    if (!src.lastScraped || src.lastScraped === 'Never') return latest;
    return latest === 'Never'
      ? src.lastScraped
      : new Date(src.lastScraped) > new Date(latest)
        ? src.lastScraped
        : latest;
  }, 'Never');
};
