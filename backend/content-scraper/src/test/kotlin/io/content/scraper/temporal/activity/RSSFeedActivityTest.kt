package io.content.scraper.temporal.activity

import io.content.scraper.models.Source
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.repository.SourceRepository
import io.content.scraper.temporal.activity.impl.RSSFeedActivityImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RSSFeedActivityTest {
    private lateinit var sourceRepository: SourceRepository
    private lateinit var articleRepository: ArticleRepository
    private lateinit var webClient: WebClient
    private lateinit var rssFeedActivity: RSSFeedActivityImpl

    @BeforeEach
    fun setup() {
        sourceRepository = mockk()
        articleRepository = mockk()
        webClient = mockk()
        rssFeedActivity = RSSFeedActivityImpl(sourceRepository, articleRepository, webClient)
    }

    @Test
    fun `should successfully fetch RSS feed`() {
        val sourceId = UUID.randomUUID()
        val source = mockk<Source>()
        val requestSpec = mockk<WebClient.RequestHeadersUriSpec<*>>()
        val responseSpec = mockk<WebClient.ResponseSpec>()

        every { source.url } returns "https://example.com/rss"
        every { sourceRepository.findById(sourceId) } returns Optional.of(source)
        every { webClient.get() } returns requestSpec
        every { requestSpec.uri("https://example.com/rss") } returns requestSpec
        every { requestSpec.retrieve() } returns responseSpec
        every { responseSpec.bodyToMono(String::class.java) } returns Mono.just("test rss content")

        val result = rssFeedActivity.fetchRSSFeed(sourceId)

        assertTrue(result.success)
        assertEquals("test rss content", result.feedContent)
    }

    @Test
    fun `should handle source not found`() {
        val sourceId = UUID.randomUUID()

        every { sourceRepository.findById(sourceId) } returns Optional.empty()

        val result = rssFeedActivity.fetchRSSFeed(sourceId)

        assertFalse(result.success)
        assertEquals("Source not found: $sourceId", result.errorMessage)
    }

    @Test
    fun `should extract article URLs from RSS content`() {
        val feedContent =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>Test Feed</title>
                    <item>
                        <title>Test Article 1</title>
                        <link>https://example.com/article1</link>
                        <description>Test description 1</description>
                    </item>
                    <item>
                        <title>Test Article 2</title>
                        <link>/article2</link>
                        <description>Test description 2</description>
                    </item>
                </channel>
            </rss>
            """.trimIndent()

        val result = rssFeedActivity.extractArticleUrls(feedContent, "https://example.com")

        assertEquals(2, result.size)
        assertEquals("Test Article 1", result[0].title)
        assertEquals("https://example.com/article1", result[0].url)
        assertEquals("Test Article 2", result[1].title)
        assertEquals("https://example.com/article2", result[1].url)
    }

    @Test
    fun `should save new articles and return IDs`() {
        val sourceId = UUID.randomUUID()
        val source = mockk<Source>()
        val articleId1 = UUID.randomUUID()
        val articleId2 = UUID.randomUUID()

        val articles =
            listOf(
                NewArticleData("Title 1", "https://example.com/1", "Desc 1", null),
                NewArticleData("Title 2", "https://example.com/2", "Desc 2", null),
            )

        every { sourceRepository.findById(sourceId) } returns Optional.of(source)
        every { articleRepository.findByUrl("https://example.com/1") } returns null
        every { articleRepository.findByUrl("https://example.com/2") } returns null
        every { articleRepository.saveAndFlush(any()) } returnsMany
            listOf(
                mockk { every { id } returns articleId1 },
                mockk { every { id } returns articleId2 },
            )

        val result = rssFeedActivity.saveNewArticles(articles, sourceId)

        assertEquals(2, result.size)
        assertTrue(result.contains(articleId1))
        assertTrue(result.contains(articleId2))
        verify(exactly = 2) { articleRepository.saveAndFlush(any()) }
    }
}
