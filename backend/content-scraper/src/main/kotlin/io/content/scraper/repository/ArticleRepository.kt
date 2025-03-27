package io.content.scraper.repository

import io.content.scraper.models.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ArticleRepository : JpaRepository<Article, UUID> {
    fun findByUrl(url: String): Article?
}
