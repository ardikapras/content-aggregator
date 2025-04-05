/**
 * Get the appropriate Bootstrap variant for sentiment display
 */
export const getSentimentVariant = (sentiment?: number): string => {
  if (!sentiment) return 'secondary';
  if (sentiment > 0.3) return 'success';
  if (sentiment < -0.3) return 'danger';
  return 'warning';
};

export const getStatusBadge = (status: string): string => {
  if (['DISCOVERED'].includes(status)) return 'secondary';
  if (['SCRAPED'].includes(status)) return 'success';
  if (['ERROR_SCRAPE'].includes(status)) return 'danger';
  return 'warning';
};

/**
 * Format date for display
 */
export const formatDate = (dateString?: string): string => {
  if (!dateString) return 'Unknown';
  try {
    return new Date(dateString).toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch (e) {
    console.error('Error formatting date:', e);
    return 'Invalid date';
  }
};
