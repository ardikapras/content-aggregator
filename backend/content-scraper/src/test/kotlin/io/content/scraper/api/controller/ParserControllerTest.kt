package io.content.scraper.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.content.scraper.models.ParserConfig
import io.content.scraper.service.ParserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(ParserController::class)
class ParserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var parserService: ParserService

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
    }

    @Test
    fun `getAllParserConfigs should return configs when successful`() {
        // Arrange
        `when`(parserService.getAllParserConfigs()).thenReturn(listOf(testConfig))

        // Act & Assert
        mockMvc
            .perform(get("/api/parser-configs"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data[0].name").value("Test Parser"))
            .andExpect(jsonPath("$.data[0].authorSelectors[0]").value("meta[name=author]"))
    }

    @Test
    fun `getAllParserConfigs should return error when exception occurs`() {
        // Arrange
        `when`(parserService.getAllParserConfigs()).thenThrow(RuntimeException("Test error"))

        // Act & Assert
        mockMvc
            .perform(get("/api/parser-configs"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Failed to retrieve parser configurations: Test error"))
    }

    @Test
    fun `getParserConfigById should return config when found`() {
        // Arrange
        `when`(parserService.getParserConfigById(testId)).thenReturn(testConfig)

        // Act & Assert
        mockMvc
            .perform(get("/api/parser-configs/$testId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("Test Parser"))
    }
}
