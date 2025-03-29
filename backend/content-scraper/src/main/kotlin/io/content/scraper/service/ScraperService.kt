package io.content.scraper.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.content.scraper.constants.KafkaTopics
import io.content.scraper.enums.ArticleStatus
import io.content.scraper.models.Article
import io.content.scraper.models.KafkaMessage
import io.content.scraper.parser.ParserManager
import io.content.scraper.parser.util.DocumentFactory
import io.content.scraper.repository.ArticleRepository
import io.github.oshai.kotlinlogging.KotlinLogging
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
                    val newsDocument = DocumentFactory.fromUrl(it.url)

                    val parserConfig = it.source.parserConfig
                    val parser =
                        if (parserConfig != null) {
                            ParserManager.getParserById(parserConfig.id)
                        } else {
                            ParserManager.getParserByName(kafkaMessage.parsingStrategy)
                        }

                    val parsedResult = parser.parse(newsDocument)

                    if (parsedResult.failure) {
                        updateArticleWithError(it, parsedResult.failureValue)
                        return
                    }

                    articleRepository.save(
                        it.copy(
                            author = parsedResult.successValue["author"],
                            content = parsedResult.successValue["content"],
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
