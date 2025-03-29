package io.content.scraper.api.controller

import io.content.scraper.api.dto.ApiResponse
import io.content.scraper.api.dto.ArticleDto
import io.content.scraper.api.dto.PageResponse
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.service.ArticleService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/articles")
class ArticleController(
    private val articleRepository: ArticleRepository,
    private val articleService: ArticleService,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping
    fun getArticles(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "publishDate") sortBy: String,
        @RequestParam(defaultValue = "DESC") direction: String,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) source: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate?,
    ): ResponseEntity<ApiResponse<PageResponse<ArticleDto>>> {
        try {
            logger.info {
                "Fetching articles with page=$page, size=$size, sortBy=$sortBy, direction=$direction, " +
                    "search=$search, source=$source, fromDate=$fromDate, toDate=$toDate"
            }

            val sortDirection =
                if (direction.equals("ASC", ignoreCase = true)) {
                    Sort.Direction.ASC
                } else {
                    Sort.Direction.DESC
                }

            val pageable =
                PageRequest.of(
                    page,
                    size,
                    Sort.by(sortDirection, sortBy),
                )

            val articlePage = articleService.findArticles(search, source, fromDate, toDate, pageable)

            val articleDtos = articlePage.content.map { ArticleDto.fromEntity(it) }

            val pageResponse =
                PageResponse(
                    content = articleDtos,
                    totalElements = articlePage.totalElements,
                    totalPages = articlePage.totalPages,
                    page = articlePage.number,
                    size = articlePage.size,
                )

            return ApiResponse.success(
                pageResponse,
                "Successfully retrieved articles",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error fetching articles: ${e.message}" }
            return ApiResponse.error("Failed to retrieve articles: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    fun getArticleById(
        @PathVariable id: String,
    ): ResponseEntity<ApiResponse<ArticleDto>> {
        try {
            val uuid =
                try {
                    UUID.fromString(id)
                } catch (e: IllegalArgumentException) {
                    return ApiResponse.error("Invalid article ID format")
                }

            return articleRepository
                .findById(uuid)
                .map { article -> ApiResponse.success(ArticleDto.fromEntity(article)) }
                .orElse(ApiResponse.notFound("Article not found with ID: $id"))
        } catch (e: Exception) {
            logger.error(e) { "Error fetching article $id: ${e.message}" }
            return ApiResponse.error("Failed to retrieve article: ${e.message}")
        }
    }

    @GetMapping("/source/{sourceId}")
    fun getArticlesBySource(
        @PathVariable sourceId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<ApiResponse<List<ArticleDto>>> {
        try {
            val uuid =
                try {
                    UUID.fromString(sourceId)
                } catch (e: IllegalArgumentException) {
                    return ApiResponse.error("Invalid source ID format")
                }

            val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishDate"))
            val articles =
                articleRepository
                    .findBySourceId(uuid, pageable)
                    .map { ArticleDto.fromEntity(it) }
                    .toList()

            return ApiResponse.success(
                articles,
                "Successfully retrieved articles for source: $sourceId",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error fetching articles for source $sourceId: ${e.message}" }
            return ApiResponse.error("Failed to retrieve articles: ${e.message}")
        }
    }
}
