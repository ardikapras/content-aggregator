-- Create extension for UUID handling
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schema
CREATE SCHEMA IF NOT EXISTS news;

-- Set default schema
SET search_path TO news, public;

CREATE TABLE sources
(
    id               UUID PRIMARY KEY            DEFAULT uuid_generate_v4(),
    name             VARCHAR(255)                                          NOT NULL,
    url              VARCHAR(2048) UNIQUE                                  NOT NULL,
    parsing_strategy VARCHAR(100)                DEFAULT 'DEFAULT',
    last_scraped     TIMESTAMP WITHOUT TIME ZONE,
    is_active        BOOLEAN                     DEFAULT TRUE              NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE articles
(
    id                   UUID PRIMARY KEY            DEFAULT uuid_generate_v4(),
    source_id            UUID                        NOT NULL,
    title                VARCHAR(512)                NOT NULL,
    url                  VARCHAR(2048)               NOT NULL,
    description          TEXT,
    content              TEXT,
    publish_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author               VARCHAR(255),
    word_count           INTEGER,
    reading_time_minutes INTEGER,
    sentiment_score      FLOAT,
    error_message        TEXT,
    retry_count          INT                         DEFAULT 0,
    last_attempt         TIMESTAMP,
    status               VARCHAR(50)                 DEFAULT 'DISCOVERED',
    created_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_source FOREIGN KEY (source_id) REFERENCES sources (id)
);

CREATE INDEX IF NOT EXISTS idx_news_items_news_source_id ON articles (source_id);
CREATE INDEX IF NOT EXISTS idx_news_items_published_at ON articles (publish_date);
CREATE INDEX IF NOT EXISTS idx_articles_status ON articles (status);
CREATE INDEX IF NOT EXISTS idx_articles_source_id ON articles (source_id);
CREATE INDEX IF NOT EXISTS idx_articles_publish_date ON articles (publish_date);
CREATE INDEX IF NOT EXISTS idx_articles_url ON articles (url);
CREATE INDEX IF NOT EXISTS idx_articles_sentiment ON articles (sentiment_score);
CREATE INDEX IF NOT EXISTS idx_news_sources_active ON sources (is_active);

CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_news_sources_timestamp
    BEFORE UPDATE
    ON sources
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_articles_timestamp
    BEFORE UPDATE
    ON articles
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- PostgreSQL database dump complete
