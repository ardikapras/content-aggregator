package io.content.scraper.service.impl

import io.content.scraper.models.Article
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.service.ArticleService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
) : ArticleService {
    private val logger = KotlinLogging.logger {}

    override fun findArticles(
        searchTerm: String?,
        sourceName: String?,
        status: String?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        pageable: Pageable,
    ): Page<Article> {
        val spec =
            Specification<Article> { root, _, criteriaBuilder ->
                val predicates = mutableListOf<Predicate>()

                if (!searchTerm.isNullOrBlank()) {
                    val searchLike = "%${searchTerm.lowercase()}%"
                    val titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root["title"]), searchLike)
                    val descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root["description"]), searchLike)
                    val contentPredicate = criteriaBuilder.like(criteriaBuilder.lower(root["content"]), searchLike)
                    val authorPredicate = criteriaBuilder.like(criteriaBuilder.lower(root["author"]), searchLike)

                    predicates.add(
                        criteriaBuilder.or(
                            titlePredicate,
                            descriptionPredicate,
                            contentPredicate,
                            authorPredicate,
                        ),
                    )
                }

                if (!sourceName.isNullOrBlank()) {
                    predicates.add(
                        criteriaBuilder.equal(
                            root.join<Article, String>("source").get<String>("name"),
                            sourceName,
                        ),
                    )
                }

                if (!status.isNullOrBlank()) {
                    predicates.add(
                        criteriaBuilder.equal(root.get<String>("status"), status),
                    )
                }

                if (fromDate != null) {
                    val fromDateTime = fromDate.atStartOfDay()
                    predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                            root["publishDate"],
                            fromDateTime,
                        ),
                    )
                }

                if (toDate != null) {
                    val toDateTime = toDate.atTime(23, 59, 59)
                    predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                            root["publishDate"],
                            toDateTime,
                        ),
                    )
                }

                criteriaBuilder.and(*predicates.toTypedArray())
            }

        return articleRepository.findAll(spec, pageable)
    }

    override fun findById(id: UUID): Article =
        articleRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Article not found with id: $id") }

    override fun findBySourceId(
        sourceId: UUID,
        pageable: Pageable,
    ): Page<Article> = articleRepository.findBySourceId(sourceId, pageable)
}
