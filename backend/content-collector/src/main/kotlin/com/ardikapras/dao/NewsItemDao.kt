package com.ardikapras.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object NewsItems : IntIdTable("news_items") {
    val newsSourceId = reference("news_source_id", NewsSources)
    val title = varchar("title", 512)
    val link = varchar("link", 2048)
    val description = text("description")
    val publishedAt = datetime("published_at")
    val contentHash = varchar("content_hash", 64) // SHA-256 hash
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}
class NewsItemDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NewsItemDao>(NewsItems)

    var newsSourceId by NewsSourcesDao referencedOn NewsItems.newsSourceId
    var title by NewsItems.title
    var link by NewsItems.link
    var description by NewsItems.description
    var publishedAt by NewsItems.publishedAt
    var contentHash by NewsItems.contentHash
    var createdAt by NewsItems.createdAt
    var updatedAt by NewsItems.updatedAt
}
