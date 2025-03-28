package io.content.scraper.service

import io.content.scraper.api.exception.NewsScraperException
import io.content.scraper.models.ParserConfig
import io.content.scraper.parser.ParserManager
import io.content.scraper.repository.ParserConfigRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ParserServiceTest {
    @Mock
    private lateinit var parserConfigRepository: ParserConfigRepository

    @InjectMocks
    private lateinit var parserService: ParserService

    @Captor
    private lateinit var parserConfigCaptor: ArgumentCaptor<ParserConfig>

    private lateinit var testConfig: ParserConfig
    private val testId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        testConfig =
            ParserConfig(
                id = testId,
                name = "Test Parser",
                description = "Test Parser Configuration",
                authorSelectors = listOf("meta[name=author]"),
                contentSelectors = listOf(".content p"),
            )
        ParserManager.initialize(listOf(testConfig))
    }

    @Test
    fun `getAllParserConfigs should return all configs from repository`() {
        // Arrange
        `when`(parserConfigRepository.findAll()).thenReturn(listOf(testConfig))

        // Act
        val result = parserService.getAllParserConfigs()

        // Assert
        assertEquals(1, result.size)
        assertEquals(testConfig, result[0])
        verify(parserConfigRepository, times(1)).findAll()
    }

    @Test
    fun `getParserConfigById should return config when exists`() {
        // Arrange
        `when`(parserConfigRepository.findById(testId)).thenReturn(Optional.of(testConfig))

        // Act
        val result = parserService.getParserConfigById(testId)

        // Assert
        assertEquals(testConfig, result)
        verify(parserConfigRepository, times(1)).findById(testId)
    }

    @Test
    fun `getParserConfigById should throw exception when config does not exist`() {
        // Arrange
        `when`(parserConfigRepository.findById(testId)).thenReturn(Optional.empty())

        // Act & Assert
        val exception =
            assertThrows(NewsScraperException::class.java) {
                parserService.getParserConfigById(testId)
            }
        assertEquals("Parser configuration not found with ID: $testId", exception.message)
    }

    @Test
    fun `getParserConfigByName should return config when exists`() {
        // Arrange
        `when`(parserConfigRepository.findByName("Test Parser")).thenReturn(testConfig)

        // Act
        val result = parserService.getParserConfigByName("Test Parser")

        // Assert
        assertEquals(testConfig, result)
        verify(parserConfigRepository, times(1)).findByName("Test Parser")
    }

    @Test
    fun `getParserConfigByName should throw exception when config does not exist`() {
        // Arrange
        `when`(parserConfigRepository.findByName("Non-existent")).thenReturn(null)

        // Act & Assert
        val exception =
            assertThrows(NewsScraperException::class.java) {
                parserService.getParserConfigByName("Non-existent")
            }
        assertEquals("Parser configuration not found with name: Non-existent", exception.message)
    }

    @Test
    fun `createParserConfig should save and return new config`() {
        // Arrange
        val newConfig = testConfig.copy(id = UUID.randomUUID(), name = "New Parser")
        `when`(parserConfigRepository.findByName(newConfig.name)).thenReturn(null)
        `when`(parserConfigRepository.save(any())).thenReturn(newConfig)

        // Act
        val result = parserService.createParserConfig(newConfig)

        // Assert
        assertEquals(newConfig, result)
        verify(parserConfigRepository, times(1)).findByName(newConfig.name)
        verify(parserConfigRepository, times(1)).save(newConfig)
    }

    @Test
    fun `createParserConfig should throw exception when name already exists`() {
        // Arrange
        `when`(parserConfigRepository.findByName(testConfig.name)).thenReturn(testConfig)

        // Act & Assert
        val exception =
            assertThrows(NewsScraperException::class.java) {
                parserService.createParserConfig(testConfig)
            }
        assertEquals("A parser configuration with name '${testConfig.name}' already exists", exception.message)
    }

    @Test
    fun `updateParserConfig should update and return config when exists`() {
        // Arrange
        val updatedConfig = testConfig.copy(description = "Updated description")
        `when`(parserConfigRepository.existsById(testId)).thenReturn(true)
        `when`(parserConfigRepository.findByName(updatedConfig.name)).thenReturn(testConfig)
        `when`(parserConfigRepository.save(any())).thenReturn(updatedConfig)

        // Act
        val result = parserService.updateParserConfig(testId, updatedConfig)

        // Assert
        assertEquals(updatedConfig, result)
        verify(parserConfigRepository, times(1)).existsById(testId)
        verify(parserConfigRepository, times(1)).findByName(updatedConfig.name)
        verify(parserConfigRepository, times(1)).save(any())
    }

    @Test
    fun `updateParserConfig should throw exception when config does not exist`() {
        // Arrange
        `when`(parserConfigRepository.existsById(testId)).thenReturn(false)

        // Act & Assert
        val exception =
            assertThrows(NewsScraperException::class.java) {
                parserService.updateParserConfig(testId, testConfig)
            }
        assertEquals("Parser configuration not found with ID: $testId", exception.message)
    }

    @Test
    fun `updateParserConfig should throw exception when name exists for different config`() {
        // Arrange
        val otherId = UUID.randomUUID()
        val otherConfig = testConfig.copy(id = otherId)
        `when`(parserConfigRepository.existsById(testId)).thenReturn(true)
        `when`(parserConfigRepository.findByName(testConfig.name)).thenReturn(otherConfig)

        // Act & Assert
        val exception =
            assertThrows(NewsScraperException::class.java) {
                parserService.updateParserConfig(testId, testConfig)
            }
        assertEquals("Another parser configuration with name '${testConfig.name}' already exists", exception.message)
    }

    @Test
    fun `deleteParserConfig should delete config when exists`() {
        // Arrange
        `when`(parserConfigRepository.existsById(testId)).thenReturn(true)
        doNothing().`when`(parserConfigRepository).deleteById(testId)

        // Act
        parserService.deleteParserConfig(testId)

        // Assert
        verify(parserConfigRepository, times(1)).existsById(testId)
        verify(parserConfigRepository, times(1)).deleteById(testId)
    }

    @Test
    fun `deleteParserConfig should throw exception when config does not exist`() {
        // Arrange
        `when`(parserConfigRepository.existsById(testId)).thenReturn(false)

        // Act & Assert
        val exception =
            assertThrows(NewsScraperException::class.java) {
                parserService.deleteParserConfig(testId)
            }
        assertEquals("Parser configuration not found with ID: $testId", exception.message)
    }
}
