package io.content.scraper.parser.engine

import io.content.scraper.models.ParserConfig
import io.content.scraper.parser.util.DocumentFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class ParserEngineTest {
    private lateinit var sampleHtml: String
    private lateinit var basicConfig: ParserConfig

    @BeforeEach
    fun setup() {
        sampleHtml =
            """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="author" content="John Doe">
                <title>Test Article</title>
                <script type="application/ld+json">
                {
                    "@context": "https://schema.org",
                    "@type": "Article",
                    "headline": "Test Article",
                    "author": {
                        "@type": "Person",
                        "name": "JSON-LD Author"
                    }
                }
                </script>
            </head>
            <body>
                <article>
                    <h1>Test Article</h1>
                    <div class="content">
                        <p>First paragraph of the article.</p>
                        <p>Second paragraph of the article.</p>
                    </div>
                    <div class="ads">
                        <p>This is an advertisement</p>
                    </div>
                    <div class="author-bio">
                        <p>Written by <span class="author-name">Element Author</span></p>
                    </div>
                </article>
            </body>
            </html>
            """.trimIndent()

        basicConfig =
            ParserConfig(
                id = UUID.randomUUID(),
                name = "Test Parser",
                description = "Test Parser Configuration",
                authorSelectors = listOf("meta[name=author]", "script[type=application/ld+json]", ".author-name"),
                contentSelectors = listOf(".content p", "article p"),
                contentFilters = listOf("advertisement"),
            )
    }

    @Test
    fun `should extract author from meta tag`() {
        val metaConfig =
            basicConfig.copy(
                authorSelectors = listOf("meta[name=author]"),
            )

        val document = DocumentFactory.fromHtml(sampleHtml)
        val engine = ParserEngine(metaConfig)
        val result = engine.parse(document)

        assertTrue(result.success)
        assertEquals("John Doe", result.successValue["author"])
    }

    @Test
    fun `should extract author from HTML element`() {
        val elementConfig =
            basicConfig.copy(
                authorSelectors = listOf(".author-name"),
            )

        val document = DocumentFactory.fromHtml(sampleHtml)
        val engine = ParserEngine(elementConfig)
        val result = engine.parse(document)

        assertTrue(result.success)
        assertEquals("Element Author", result.successValue["author"])
    }

    @Test
    fun `should try selectors in order until match is found`() {
        val orderedConfig =
            basicConfig.copy(
                authorSelectors = listOf(".author-name", "does-not-exist", "meta[name=author]"),
            )

        val document = DocumentFactory.fromHtml(sampleHtml)
        val engine = ParserEngine(orderedConfig)
        val result = engine.parse(document)

        assertTrue(result.success)
        assertEquals("Element Author", result.successValue["author"])
    }

    @Test
    fun `should extract content with proper selectors`() {
        val document = DocumentFactory.fromHtml(sampleHtml)
        val engine = ParserEngine(basicConfig)
        val result = engine.parse(document)

        assertTrue(result.success)
        val content = result.successValue["content"]
        assertNotNull(content)
        assertTrue(content!!.contains("First paragraph"))
        assertTrue(content.contains("Second paragraph"))

        val expectedContent = "First paragraph of the article.\n\nSecond paragraph of the article."
        assertEquals(expectedContent, content)
    }

    @Test
    fun `should filter content based on filters`() {
        val htmlWithFilteredContent =
            """
            <div class="content">
                <p>Good content.</p>
                <p>This contains advertisement content.</p>
            </div>
            """.trimIndent()

        val document = DocumentFactory.fromHtml(htmlWithFilteredContent)
        val engine = ParserEngine(basicConfig)

        val result = engine.parse(document)

        val content = result.successValue["content"] ?: ""
        assertTrue(content.contains("Good content"))
        assertFalse(content.contains("advertisement content"))
    }

    @Test
    fun `should return empty content if no selectors match`() {
        val config =
            basicConfig.copy(
                contentSelectors = listOf(".non-existent-selector"),
            )

        val document = DocumentFactory.fromHtml(sampleHtml)
        val engine = ParserEngine(config)
        val result = engine.parse(document)

        assertTrue(result.success)
        assertEquals("", result.successValue["content"])
    }

    @Test
    fun `should return 'Unknown' author if no selectors match`() {
        val config =
            basicConfig.copy(
                authorSelectors = listOf(".non-existent-selector"),
            )

        val document = DocumentFactory.fromHtml(sampleHtml)
        val engine = ParserEngine(config)
        val result = engine.parse(document)

        assertTrue(result.success)
        assertEquals("Unknown", result.successValue["author"])
    }
}
