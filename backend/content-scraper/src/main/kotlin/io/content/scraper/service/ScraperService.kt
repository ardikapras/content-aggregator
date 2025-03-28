package io.content.scraper.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.content.scraper.constant.KafkaTopics
import io.content.scraper.enum.ArticleStatus
import io.content.scraper.models.Article
import io.content.scraper.models.KafkaMessage
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.strategy.util.NewsStrategyManager
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScraperService(
    private val articleRepository: ArticleRepository,
    private val objectMapper: ObjectMapper,
) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = [KafkaTopics.CONTENT_TO_SCRAPE])
    fun consumeArticle(message: String) {
        logger.debug { "Consuming message: $message" }
        try {
            val kafkaMessage = objectMapper.readValue(message, KafkaMessage::class.java)
            processArticle(kafkaMessage)
        } catch (e: Exception) {
            logger.error(e) { "Error processing message: ${e.message}" }
        }
    }

    private fun processArticle(kafkaMessage: KafkaMessage) {
        articleRepository
            .findById(kafkaMessage.id)
            .orElse(null)
            ?.let {
                try {
                    val newsDocument = Jsoup.connect(it.url).get()
                    val strategy = NewsStrategyManager.getStrategy(kafkaMessage.parsingStrategy)

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
