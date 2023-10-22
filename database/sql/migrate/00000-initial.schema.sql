--liquibase formatted sql
--changeset ardikapras:initial-1 logicalFilePath:path-independent

CREATE TABLE news_sources
(
    id                SERIAL PRIMARY KEY,
    name              VARCHAR(255)                                          NOT NULL,
    endpoint_url      VARCHAR(2048) UNIQUE                                  NOT NULL,
    is_active         BOOLEAN                     DEFAULT TRUE              NOT NULL,
    last_collected_at TIMESTAMP WITHOUT TIME ZONE,
    created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE news_items
(
    id              SERIAL PRIMARY KEY,
    news_source_id  INT REFERENCES news_sources (id),
    title           VARCHAR(512)                NOT NULL,
    link            VARCHAR(2048)               NOT NULL,
    description     TEXT,
    published_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    content_hash    VARCHAR(64) UNIQUE          NOT NULL, -- Ensuring the uniqueness of news based on the content hash
    created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_news_items_news_source_id ON news_items (news_source_id);
CREATE INDEX idx_news_items_published_at ON news_items (published_at);


-- PostgreSQL database dump complete
