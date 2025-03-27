package io.content.scraper.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.content.scraper.constant.KafkaTopics
import io.content.scraper.dto.ArticleDto
import io.content.scraper.models.Article
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.strategy.NewsStrategyManager.parseStrategyMap
import io.github.oshai.kotlinlogging.KotlinLogging
import io.news.scraper.core.enum.ArticleStatus
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScraperService(
    private val articleRepository: ArticleRepository,
    private val objectMapper: ObjectMapper,
    @Value("\${app.max-retry-count:3}") private val maxRetryCount: Int,
) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = [KafkaTopics.CONTENT_TO_SCRAPE])
    fun consumeArticle(message: String) {
        logger.debug { "Consuming message: $message" }
        try {
            val articleDto = objectMapper.readValue(message, ArticleDto::class.java)
            processArticle(articleDto)
        } catch (e: Exception) {
            logger.error(e) { "Error processing message: ${e.message}" }
        }
    }

    private fun processArticle(articleDto: ArticleDto) {
        articleRepository
            .findById(articleDto.id)
            .orElse(null)
            ?.takeIf { it.retryCount < maxRetryCount }
            ?.let {
                try {
                    val newsDocument = Jsoup.connect(it.url).get()
                    val strategy = parseStrategyMap[articleDto.parsingStrategy]

                    if (strategy == null) {
                        updateArticleWithError(it, "No strategy found for: ${articleDto.parsingStrategy}")
                        return
                    }

                    val parsedNews = strategy.parse(newsDocument)

                    if (parsedNews.failure) {
                        updateArticleWithError(it, parsedNews.failureValue)
                        return
                    }

                    articleRepository.save(
                        it.copy(
                            author = parsedNews.successValue["author"],
                            content = parsedNews.successValue["content"],
                            status = ArticleStatus.SCRAPED.name,
                        ),
                    )
                } catch (e: Exception) {
                    updateArticleWithError(it, e.message ?: "Unknown error")
                    logger.error(e) { "Error processing article: ${it.id}" }
                }
            }
    }

    private fun updateArticleWithError(
        article: Article,
        errorMessage: String,
    ) {
        articleRepository.save(
            article.copy(
                errorMessage = errorMessage,
                status = ArticleStatus.ERROR_SCRAPE.name,
                retryCount = article.retryCount + 1,
                lastAttempt = LocalDateTime.now(),
            ),
        )
    }
}
