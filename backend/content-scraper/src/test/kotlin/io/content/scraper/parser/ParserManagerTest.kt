package io.content.scraper.parser

import io.content.scraper.models.ParserConfig
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.test.util.ReflectionTestUtils
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ParserManagerTest {
    private lateinit var defaultConfig: ParserConfig
    private lateinit var testConfig: ParserConfig
    private val testId = UUID.randomUUID()
    private val defaultId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        defaultConfig =
            ParserConfig(
                id = defaultId,
                name = "DEFAULT",
                description = "Default Configuration",
                authorSelectors = listOf("meta[name=author]"),
                contentSelectors = listOf(".content p"),
            )

        testConfig =
            ParserConfig(
                id = testId,
                name = "Test Parser",
                description = "Test Configuration",
                authorSelectors = listOf(".author"),
                contentSelectors = listOf(".article p"),
            )
    }

    @Test
    fun `initialize should load configs into cache`() {
        // Act
        ParserManager.initialize(listOf(defaultConfig, testConfig))

        // Assert
        val configCacheById = ReflectionTestUtils.getField(ParserManager, "configCacheById") as Map<*, *>
        val configCacheByName = ReflectionTestUtils.getField(ParserManager, "configCacheByName") as Map<*, *>

        assertEquals(2, configCacheById.size)
        assertEquals(2, configCacheByName.size)
        assertTrue(configCacheById.containsKey(defaultId))
        assertTrue(configCacheById.containsKey(testId))
        assertTrue(configCacheByName.containsKey("DEFAULT"))
        assertTrue(configCacheByName.containsKey("Test Parser"))
    }

    @Test
    fun `initialize should set default config when present`() {
        // Act
        ParserManager.initialize(listOf(defaultConfig, testConfig))

        // Assert
        val defaultConfigField = ReflectionTestUtils.getField(ParserManager, "defaultConfig") as ParserConfig
        assertEquals(defaultId, defaultConfigField.id)
        assertEquals("DEFAULT", defaultConfigField.name)
    }

    @Test
    fun `updateConfig should add or update config in cache`() {
        // Arrange
        ParserManager.initialize(listOf(defaultConfig))

        // Act
        ParserManager.updateConfig(testConfig)

        // Assert
        val configCacheById = ReflectionTestUtils.getField(ParserManager, "configCacheById") as Map<*, *>
        val configCacheByName = ReflectionTestUtils.getField(ParserManager, "configCacheByName") as Map<*, *>

        assertTrue(configCacheById.containsKey(testId))
        assertTrue(configCacheByName.containsKey("Test Parser"))
    }

    @Test
    fun `getParserByName should return parser with correct config`() {
        // Arrange
        ParserManager.initialize(listOf(defaultConfig, testConfig))

        // Act
        val parser = ParserManager.getParserByName("Test Parser")

        // Assert
        assertNotNull(parser)

        val testHtml =
            """
            <html>
            <head><title>Test</title></head>
            <body>
                <div class="author">Test Author</div>
                <div class="article"><p>Test Content</p></div>
            </body>
            </html>
            """.trimIndent()

        val document = Jsoup.parse(testHtml)
        val result = parser.parse(document)

        assertTrue(result.success)
        assertEquals("Test Author", result.successValue["author"])
        assertEquals("Test Content", result.successValue["content"])
    }

    @Test
    fun `getParserByName should return parser with default config when name not found`() {
        // Arrange
        ParserManager.initialize(listOf(defaultConfig))

        // Act
        val parser = ParserManager.getParserByName("Non-existent")

        // Assert
        assertNotNull(parser)

        val testHtml =
            """
            <html>
            <head><meta name="author" content="Default Author"><title>Test</title></head>
            <body>
                <div class="content"><p>Default Content</p></div>
            </body>
            </html>
            """.trimIndent()

        val document = Jsoup.parse(testHtml)
        val result = parser.parse(document)

        assertTrue(result.success)
        assertEquals("Default Author", result.successValue["author"])
        assertEquals("Default Content", result.successValue["content"])
    }

    @Test
    fun `getParserById should return parser with correct config`() {
        // Arrange
        ParserManager.initialize(listOf(defaultConfig, testConfig))

        // Act
        val parser = ParserManager.getParserById(testId)

        // Assert
        assertNotNull(parser)

        val testHtml =
            """
            <html>
            <head><title>Test</title></head>
            <body>
                <div class="author">Test Author</div>
                <div class="article"><p>Test Content</p></div>
            </body>
            </html>
            """.trimIndent()

        val document = Jsoup.parse(testHtml)
        val result = parser.parse(document)

        assertTrue(result.success)
        assertEquals("Test Author", result.successValue["author"])
        assertEquals("Test Content", result.successValue["content"])
    }

    @Test
    fun `parseWithConfig should parse document using specified config by ID`() {
        // Arrange
        ParserManager.initialize(listOf(defaultConfig, testConfig))

        val testHtml =
            """
            <html>
            <head><title>Test</title></head>
            <body>
                <div class="author">Test Author</div>
                <div class="article"><p>Test Content</p></div>
            </body>
            </html>
            """.trimIndent()

        val document = Jsoup.parse(testHtml)

        // Act
        val result = ParserManager.parseWithConfig(document, testId)

        // Assert
        assertTrue(result.success)
        assertEquals("Test Author", result.successValue["author"])
        assertEquals("Test Content", result.successValue["content"])
    }

    @Test
    fun `parseWithConfig should parse document using specified config by name`() {
        // Arrange
        ParserManager.initialize(listOf(defaultConfig, testConfig))

        val testHtml =
            """
            <html>
            <head><title>Test</title></head>
            <body>
                <div class="author">Test Author</div>
                <div class="article"><p>Test Content</p></div>
            </body>
            </html>
            """.trimIndent()

        val document = Jsoup.parse(testHtml)

        // Act
        val result = ParserManager.parseWithConfig(document, "Test Parser")

        // Assert
        assertTrue(result.success)
        assertEquals("Test Author", result.successValue["author"])
        assertEquals("Test Content", result.successValue["content"])
    }
}
