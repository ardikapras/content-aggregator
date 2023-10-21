package com.ardikapras.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object NewsSources : IntIdTable("news_sources") {
    val name: Column<String> = varchar("name", 255)
    val endpointUrl: Column<String> = varchar("endpoint_url", 2048).uniqueIndex()
    val isActive: Column<Boolean> = bool("is_active").default(true)
    val lastCollectedAt: Column<LocalDateTime?> = datetime("last_collected_at").nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
}
