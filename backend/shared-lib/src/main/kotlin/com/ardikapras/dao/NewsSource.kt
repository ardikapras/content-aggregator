package com.ardikapras.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object NewsSources : IntIdTable("news_sources") {
    val name: Column<String> = varchar("name", 255)
    val endpointUrl: Column<String> = varchar("endpoint_url", 2048).uniqueIndex()
    val parsingStrategy: Column<String> = varchar("parsing_strategy", 255)
    val isActive: Column<Boolean> = bool("is_active").default(true)
    val lastCollectedAt: Column<LocalDateTime?> = datetime("last_collected_at").nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
}

class NewsSource(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NewsSource>(NewsSources)

    var name by NewsSources.name
    var endpointUrl by NewsSources.endpointUrl
    var isActive by NewsSources.isActive
    var parsingStrategy by NewsSources.parsingStrategy
    var lastCollectedAt by NewsSources.lastCollectedAt
    var createdAt by NewsSources.createdAt
    var updatedAt by NewsSources.updatedAt
}
