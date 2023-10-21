package com.ardikapras.database.dao

import com.ardikapras.database.table.NewsSources
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class NewsSourcesDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<com.ardikapras.database.dao.NewsSourcesDao>(NewsSources)

    var name by NewsSources.name
    var endpointUrl by NewsSources.endpointUrl
    var isActive by NewsSources.isActive
    var lastCollectedAt by NewsSources.lastCollectedAt
    var createdAt by NewsSources.createdAt
    var updatedAt by NewsSources.updatedAt
}
