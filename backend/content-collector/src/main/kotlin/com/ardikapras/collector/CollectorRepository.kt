package com.ardikapras.collector

import com.ardikapras.database.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

class CollectorRepository {
    fun getAllActiveNewsSources(): List<NewsSourcesDao> = transaction {
        NewsSourcesDao.all().filter { it.isActive }.toList()
    }

    fun isRawRssDataExist(hash: String): Boolean {
        return transaction {
            RawRssDataDao.find { RawRssData.contentHash eq hash }.count() > 0
        }
    }

    fun isNewsItemExist(hash: String): Boolean {
        return transaction {
            NewsItemDao.find { NewsItems.contentHash eq hash }.count() > 0
        }
    }

    fun insertRawRssData(newsSourceId: Int, rssContent: String, contentHash: String): Int {
        return transaction {
            val rawRssData = RawRssDataDao.new {
                this.newsSourceId = NewsSourcesDao[newsSourceId]
                this.rssContent = rssContent
                this.contentHash = contentHash
            }
            rawRssData.id.value
        }
    }

    fun insertNewsItem(newsSourceId: Int, rawRssDataId: Int, title: String, link: String, description: String, publishedDate: LocalDateTime, hash: String) {
        transaction {
            NewsItemDao.new {
                this.newsSourceId = NewsSourcesDao[newsSourceId]
                this.rawRssDataId = RawRssDataDao[rawRssDataId]
                this.title = title
                this.link = link
                this.description = description
                this.publishedAt = publishedDate
                this.contentHash = hash
            }
        }
    }
}
