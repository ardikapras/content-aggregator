package io.content.scraper.enums

enum class ActivityLogStatus {
    SUCCESS,
    FAILED,
}

enum class ActivityLogAction {
    SCRAPE,
    RETRY_SCRAPE,
    PARSER_CONFIG,
}
