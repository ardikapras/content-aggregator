SET search_path TO news, public;

-- Create parser_configs table for storing configurable parser settings
CREATE TABLE IF NOT EXISTS news.parser_configs
(
    id                 UUID PRIMARY KEY            DEFAULT uuid_generate_v4(),
    name               VARCHAR(255)                                          NOT NULL UNIQUE,
    description        TEXT,
    author_selectors   TEXT[], -- Array of CSS selectors to extract author
    content_selectors  TEXT[], -- Array of CSS selectors to extract content
    next_page_selector TEXT,   -- CSS selector for the next page link (if multi-page)
    content_filters    TEXT[], -- Array of regex patterns to filter out from content
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create trigger for updated_at
CREATE TRIGGER update_parser_configs_timestamp
    BEFORE UPDATE
    ON news.parser_configs
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Add parser_config_id column to sources table
ALTER TABLE news.sources
    ADD COLUMN parser_config_id UUID REFERENCES news.parser_configs (id);

-- Create initial parser configurations from existing strategies
INSERT INTO news.parser_configs (id, name, description, author_selectors, content_selectors, content_filters)
VALUES ('2b852f78-e03f-4764-858f-6d1b66d861b4', 'ANTARA', 'Parser configuration for Antara News',
        ARRAY ['script[type=application/ld+json]'],
        ARRAY ['div.post-content'],
        ARRAY ['span.baca-juga', 'p.text-muted']),

       ('89cac3df-0e75-4a18-816e-8d2ad109ebe6', 'CNBC', 'Parser configuration for CNBC Indonesia',
        ARRAY ['script[type=application/ld+json]'],
        ARRAY ['.detail-text', 'article p', '.article-content p', '.content p'],
        ARRAY ['.media-institusi']),

       ('7098edda-9a37-4e54-8e9c-e96f929cbe83', 'CNN', 'Parser configuration for CNN Indonesia',
        ARRAY ['meta[name=author]', 'meta[name=content_author]'],
        ARRAY ['.detail-text p', '.detail-wrap p', '.content-artikel p', '.content p', 'article p'],
        ARRAY ['.para_caption']),

       ('730c8461-9643-4a9a-a8ac-c2ebdf159939', 'JPNN', 'Parser configuration for JPNN',
        ARRAY ['meta[name=jpnncom_news_author]'],
        ARRAY ['div[itemprop=articleBody] p', '.page-content p'],
        ARRAY ['JPNN\\.com.*', '^Baca juga.*']),

       ('b5214885-6b56-46bb-aea6-a4403e4f2f94', 'DEFAULT', 'Default parser configuration',
        ARRAY ['script[type=application/ld+json]', 'meta[name=author]'],
        ARRAY ['.content p', 'main p', 'article p', '.article-content p'],
        ARRAY []::varchar[]);

-- Update existing sources to use the corresponding parser config
UPDATE news.sources s
SET parser_config_id = (SELECT id FROM news.parser_configs pc WHERE pc.name = s.parsing_strategy)
WHERE s.parsing_strategy IN ('ANTARA', 'CNBC', 'CNN', 'JPNN', 'DEFAULT');
