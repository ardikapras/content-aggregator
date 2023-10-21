package com.ardikapras.collector

import org.jetbrains.exposed.sql.transactions.transaction
import com.ardikapras.database.dao.NewsSourcesDao
import com.ardikapras.database.table.NewsSources

class CollectorRepository {
    fun getAllActiveNewsSources(): List<NewsSourcesDao> = transaction {
        NewsSourcesDao.all().filter { it.isActive }.toList()
    }

    fun findNewsSourceById(id: Int): NewsSourcesDao? = transaction {
        NewsSourcesDao.findById(id)
    }

    fun findNewsSourceByName(name: String): List<NewsSourcesDao> = transaction {
        NewsSourcesDao.find { NewsSources.name eq name }.toList()
    }
}
