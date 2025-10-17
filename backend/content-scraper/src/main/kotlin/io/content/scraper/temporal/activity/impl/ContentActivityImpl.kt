package io.content.scraper.temporal.activity.impl

import io.content.scraper.parser.ParserManager
import io.content.scraper.parser.util.DocumentFactory
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.service.MetricsService
import io.content.scraper.temporal.activity.ContentActivity
import io.content.scraper.temporal.types.ContentMetadata
import io.content.scraper.temporal.types.ContentParsingRequest
import io.content.scraper.temporal.types.ContentParsingResult
import io.content.scraper.temporal.types.MultiPageParsingRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import io.temporal.activity.Activity
import org.springframework.stereotype.Component

@Component
class ContentActivityImpl(
    private val articleRepository: ArticleRepository,
    private val metricsService: MetricsService,
) : ContentActivity {
    private val logger = KotlinLogging.logger {}

    override fun parseArticleContent(request: ContentParsingRequest): ContentParsingResult {
        Activity.getExecutionContext().heartbeat("Parsing article content: ${request.articleId}")
        metricsService.recordTemporalActivityExecuted("ParseArticleContent", "started")

        return try {
            val article =
                articleRepository.findById(request.articleId).orElse(null)
                    ?: return ContentParsingResult(false, null, null, 1, "Article not found").also {
                        metricsService.recordTemporalActivityExecuted("ParseArticleContent", "failed")
                        metricsService.recordArticleFailed(errorType = "article_not_found")
                    }

            metricsService.timeArticleProcessing(article.source.name) {
                val newsDocument = DocumentFactory.fromUrl(request.url)
                logger.debug { "Fetched document for article: ${request.articleId}" }

                val parser =
                    if (article.source.parserConfig != null) {
                        ParserManager.getParserById(article.source.parserConfig!!.id)
                    } else {
                        ParserManager.getParserByName(request.parsingStrategy)
                    }

                val parsedResult = parser.parse(newsDocument)

                if (parsedResult.failure) {
                    logger.error { "Parsing failed for article: ${request.articleId} - ${parsedResult.failureValue}" }
                    metricsService.recordTemporalActivityExecuted("ParseArticleContent", "failed")
                    metricsService.recordArticleFailed(article.source.name, "parsing_error")
                    return@timeArticleProcessing ContentParsingResult(
                        success = false,
                        content = null,
                        author = null,
                        extractedPages = 1,
                        errorMessage = parsedResult.failureValue,
                    )
                }

                metricsService.recordTemporalActivityExecuted("ParseArticleContent", "completed")
                metricsService.recordArticleProcessed(article.source.name)

                logger.debug { "Successfully parsed article: ${request.articleId}" }
                ContentParsingResult(
                    success = true,
                    content = parsedResult.successValue["content"],
                    author = parsedResult.successValue["author"],
                    extractedPages = 1,
                    errorMessage = null,
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Error parsing article: ${request.articleId}" }
            metricsService.recordTemporalActivityExecuted("ParseArticleContent", "failed")
            metricsService.recordArticleFailed(errorType = "exception")
            ContentParsingResult(
                success = false,
                content = null,
                author = null,
                extractedPages = 1,
                errorMessage = e.message ?: "Unknown parsing error",
            )
        }
    }

    override fun parseMultiPageArticle(request: MultiPageParsingRequest): ContentParsingResult {
        Activity.getExecutionContext().heartbeat("Parsing multi-page article: ${request.articleId}")

        return try {
            val article =
                articleRepository.findById(request.articleId).orElse(null)
                    ?: return ContentParsingResult(false, null, null, 0, "Article not found")

            val parser =
                if (article.source.parserConfig != null) {
                    ParserManager.getParserById(article.source.parserConfig!!.id)
                } else {
                    ParserManager.getParserByName(request.parsingStrategy)
                }

            val allContent = StringBuilder()
            var author: String? = null
            var currentUrl = request.initialUrl
            var pagesProcessed = 0

            while (pagesProcessed < request.maxPages) {
                Activity.getExecutionContext().heartbeat("Processing page ${pagesProcessed + 1} of article: ${request.articleId}")

                val newsDocument = DocumentFactory.fromUrl(currentUrl)
                val parsedResult = parser.parse(newsDocument)

                if (parsedResult.failure) {
                    if (pagesProcessed == 0) {
                        return ContentParsingResult(
                            success = false,
                            content = null,
                            author = null,
                            extractedPages = pagesProcessed,
                            errorMessage = parsedResult.failureValue,
                        )
                    }
                    break
                }

                parsedResult.successValue["content"]?.let { content ->
                    allContent.append(content)
                    if (pagesProcessed > 0) allContent.append("\n\n")
                }

                if (author == null) {
                    author = parsedResult.successValue["author"]
                }

                pagesProcessed++

                // Check for next page link based on parser strategy
                val nextPageUrl = extractNextPageUrl(parsedResult.successValue, currentUrl, parser)
                if (nextPageUrl != null && nextPageUrl != currentUrl) {
                    currentUrl = nextPageUrl
                    logger.debug { "Found next page URL for article ${request.articleId}: $nextPageUrl" }
                } else {
                    logger.debug { "No more pages found for article ${request.articleId}" }
                    break
                }
            }

            ContentParsingResult(
                success = true,
                content = allContent.toString(),
                author = author,
                extractedPages = pagesProcessed,
                errorMessage = null,
            )
        } catch (e: Exception) {
            logger.error(e) { "Error parsing multi-page article: ${request.articleId}" }
            ContentParsingResult(
                success = false,
                content = null,
                author = null,
                extractedPages = 0,
                errorMessage = e.message ?: "Unknown parsing error",
            )
        }
    }

    override fun extractMetadata(content: String): ContentMetadata {
        Activity.getExecutionContext().heartbeat("Extracting metadata from content")

        return try {
            val wordCount = content.split("\\s+".toRegex()).size
            val readingTimeMinutes = maxOf(1, wordCount / 200) // Assuming 200 words per minute

            ContentMetadata(
                wordCount = wordCount,
                readingTimeMinutes = readingTimeMinutes,
                language = null, // Could implement language detection here
                tags = emptyList(), // Could implement tag extraction here
            )
        } catch (e: Exception) {
            logger.error(e) { "Error extracting metadata" }
            ContentMetadata()
        }
    }

    private fun extractNextPageUrl(
        parseResult: Map<String, String>,
        currentUrl: String,
        parser: Any, // Replace with actual parser type when available
    ): String? {
        // Try to extract next page URL from parse results
        val nextPageFromResult = parseResult["nextPageUrl"]
        if (!nextPageFromResult.isNullOrBlank()) {
            return resolveUrl(nextPageFromResult, currentUrl)
        }

        // If parser has specific multi-page logic, it would be called here
        // For now, we check common patterns in the parsed content
        val content = parseResult["content"]
        if (!content.isNullOrBlank()) {
            return extractNextPageFromContent(content, currentUrl)
        }

        return null
    }

    private fun extractNextPageFromContent(
        content: String,
        baseUrl: String,
    ): String? {
        // Look for common "next page" patterns in content
        val nextPagePatterns =
            listOf(
                "(?i)href=[\"']([^\"']*(?:next|continue|page\\s*2)[^\"']*)[\"']",
                "(?i)href=[\"']([^\"']*page[=/]\\d+[^\"']*)[\"']",
            )

        for (pattern in nextPagePatterns) {
            val regex = pattern.toRegex()
            val match = regex.find(content)
            if (match != null) {
                val nextPageUrl = match.groupValues[1]
                return resolveUrl(nextPageUrl, baseUrl)
            }
        }

        return null
    }

    private fun resolveUrl(
        url: String,
        baseUrl: String,
    ): String =
        when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("/") -> {
                val base = java.net.URI(baseUrl)
                "${base.scheme}://${base.host}${if (base.port != -1) ":${base.port}" else ""}$url"
            }
            else -> {
                val base = java.net.URI(baseUrl)
                val basePath = base.path.substringBeforeLast("/")
                "${base.scheme}://${base.host}${if (base.port != -1) ":${base.port}" else ""}$basePath/$url"
            }
        }
}
