package com.ardikapras.database.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object RawRssData : IntIdTable("raw_rss_data") {
    val newsSourceId = reference("news_source_id", NewsSources)
    val status = varchar("status", 2048).default("success")
    val errorMessage = text("error_message").default("")
    val rssContent = text("rss_content")
    val contentHash = varchar("content_hash", 64)
    val collectedAt = datetime("collected_at").default(LocalDateTime.now())
}
class RawRssDataDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RawRssDataDao>(RawRssData)

    var newsSourceId by NewsSourcesDao referencedOn RawRssData.newsSourceId
    var status by RawRssData.status
    var errorMessage by RawRssData.errorMessage
    var rssContent by RawRssData.rssContent
    var contentHash by RawRssData.contentHash
    var collectedAt by RawRssData.collectedAt
}
