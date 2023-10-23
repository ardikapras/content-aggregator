package com.ardikapras.collector

import com.ardikapras.dao.NewsItem
import com.ardikapras.dao.NewsItems
import com.ardikapras.dao.NewsSource
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CollectorRepository {
    fun getAllActiveNewsSources(): List<NewsSource> = transaction {
        NewsSource.all().filter { it.isActive }.toList()
    }

    fun isNewsItemExist(hash: String): Boolean {
        return transaction {
            NewsItem.find { NewsItems.contentHash eq hash }.count() > 0
        }
    }

    fun insertNewsItem(
        newsSourceId: Int,
        title: String,
        link: String,
        description: String,
        publishedDate: LocalDateTime,
        hash: String,
    ): Int {
        return transaction {
            val newsItem = NewsItem.new {
                this.newsSourceId = NewsSource[newsSourceId]
                this.title = title
                this.link = link
                this.description = description
                this.publishedAt = publishedDate
                this.contentHash = hash
            }

            newsItem.id.value
        }
    }
}
