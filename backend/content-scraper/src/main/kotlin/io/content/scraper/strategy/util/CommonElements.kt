package io.content.scraper.strategy.util

/**
 * Common elements to remove across different news sites
 */
object CommonElements {
    val COMMON_ELEMENTS_TO_REMOVE =
        mapOf(
            "advertisements" to
                listOf(
                    "div[id*=div-gpt-ad]",
                    "ins.adsbygoogle",
                    "script",
                    "div[style*=advertisement]",
                    "[id*=div-gpt-ad]",
                    ".banner",
                ),
            "relatedContent" to
                listOf(
                    "div.baca-juga",
                    ".linksisip",
                    ".baca-juga",
                    ".terkait",
                ),
            "socialSharing" to
                listOf(
                    ".detail-share",
                    ".share",
                    ".top-medsos",
                    ".creator-medsos",
                ),
            "multimedia" to
                listOf(
                    ".video_detail",
                    "iframe",
                ),
            "navigation" to
                listOf(
                    ".tags",
                    ".pagination",
                ),
            "comments" to
                listOf(
                    "#disqus_thread",
                    "#disqus-comments",
                    "#komentar",
                ),
        )

    val COMMON_CONTENT_SELECTORS =
        listOf(
            ".detail-text p",
            ".detail-wrap p",
            ".content-artikel p",
            ".article-content p",
            ".content p",
            "article p",
            "div[itemprop=articleBody] p",
        )

    val COMMON_META_AUTHOR_SELECTORS =
        listOf(
            "author",
            "jpnncom_news_author",
            "content_author",
            "news:author",
        )

    val COMMON_TEXT_EXCLUDES =
        listOf(
            Regex(".*\\([a-z]{2,3}/[a-z]{2,3}\\).*"), // Exclude agency markers like (jp/abc)
            Regex("^Baca juga.*", RegexOption.IGNORE_CASE),
            Regex("^Baca berita.*"),
            Regex("^Silakan baca.*"),
            Regex("^JPNN\\.com.*"),
            Regex("^[a-zA-Z0-9]+\\.jpnn\\.com.*"),
        )
}
