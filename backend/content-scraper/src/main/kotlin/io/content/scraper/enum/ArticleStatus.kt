package io.content.scraper.enum

enum class ArticleStatus {
    DISCOVERED, // URL found during scraping
    SCRAPED, // Content extracted but not processed
    ENRICHED, // Content analysis, category inference done
    PROCESSED, // All processing completed successfully
    ERROR_SCRAPE, // Error during scraping phase
    ERROR_PROCESS, // Error during enrichment/processing
    BLACKLISTED, // Exceeded retry limit, won't attempt again
}
