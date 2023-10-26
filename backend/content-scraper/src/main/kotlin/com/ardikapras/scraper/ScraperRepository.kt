package com.ardikapras.scraper

import com.ardikapras.dao.NewsItem
import org.jetbrains.exposed.sql.transactions.transaction

class ScraperRepository {
    fun updateNewsItem(
        id: Int,
        content: String,
    ) {
        transaction {
            val newsItem = NewsItem.findById(id)
            if (newsItem != null) {
                newsItem.content = content
            }
        }
    }
}
