package com.ardikapras.collector

import com.ardikapras.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CollectorRepository {
    fun getAllActiveNewsSources(): List<NewsSourcesDao> = transaction {
        NewsSourcesDao.all().filter { it.isActive }.toList()
    }

    fun isNewsItemExist(hash: String): Boolean {
        return transaction {
            NewsItemDao.find { NewsItems.contentHash eq hash }.count() > 0
        }
    }

    fun insertNewsItem(
        newsSourceId: Int,
        title: String,
        link: String,
        description: String,
        publishedDate: LocalDateTime,
        hash: String,
    ) {
        transaction {
            NewsItemDao.new {
                this.newsSourceId = NewsSourcesDao[newsSourceId]
                this.title = title
                this.link = link
                this.description = description
                this.publishedAt = publishedDate
                this.contentHash = hash
            }
        }
    }
}
