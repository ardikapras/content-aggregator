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
-- Antara
insert into news_sources (name, endpoint_url) values ('Antara News Terkini', 'https://www.antaranews.com/rss/terkini.xml');
insert into news_sources (name, endpoint_url) values ('Antara News Galeri Photo', 'https://www.antaranews.com/rss/photo.xml');
insert into news_sources (name, endpoint_url) values ('Antara News Video', 'https://www.antaranews.com/rss/video.xml');
insert into news_sources (name, endpoint_url) values ('Antara Top News', 'https://www.antaranews.com/rss/top-news.xml');
insert into news_sources (name, endpoint_url) values ('Antara Politik', 'https://www.antaranews.com/rss/politik.xml');
insert into news_sources (name, endpoint_url) values ('Antara Hukum', 'https://www.antaranews.com/rss/hukum.xml');
insert into news_sources (name, endpoint_url) values ('Antara Ekonomi', 'https://www.antaranews.com/rss/ekonomi.xml');
insert into news_sources (name, endpoint_url) values ('Antara Metro', 'https://www.antaranews.com/rss/metro.xml');
insert into news_sources (name, endpoint_url) values ('Antara Sepakbola', 'https://www.antaranews.com/rss/sepakbola.xml');
insert into news_sources (name, endpoint_url) values ('Antara Olahraga', 'https://www.antaranews.com/rss/olahraga.xml');
insert into news_sources (name, endpoint_url) values ('Antara Humaniora', 'https://www.antaranews.com/rss/humaniora.xml');
insert into news_sources (name, endpoint_url) values ('Antara Lifestyle', 'https://www.antaranews.com/rss/lifestyle.xml');
insert into news_sources (name, endpoint_url) values ('Antara Hiburan', 'https://www.antaranews.com/rss/hiburan.xml');
insert into news_sources (name, endpoint_url) values ('Antara Dunia', 'https://www.antaranews.com/rss/dunia.xml');
insert into news_sources (name, endpoint_url) values ('Antara Infografik', 'https://www.antaranews.com/rss/infografik.xml');
insert into news_sources (name, endpoint_url) values ('Antara Tekno', 'https://www.antaranews.com/rss/tekno.xml');
insert into news_sources (name, endpoint_url) values ('Antara Otomotif', 'https://www.antaranews.com/rss/otomotif.xml');
insert into news_sources (name, endpoint_url) values ('Antara Warta Bumi', 'https://www.antaranews.com/rss/warta-bumi.xml');
insert into news_sources (name, endpoint_url) values ('Antara Rilis Pers', 'https://www.antaranews.com/rss/rilis-pers.xml');

-- CNBC Indonesia
insert into news_sources (name, endpoint_url) values ('CNBC ID - Market', 'https://www.cnbcindonesia.com/market/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - News', 'https://www.cnbcindonesia.com/news/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Entrepreneur', 'https://www.cnbcindonesia.com/entrepreneur/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Syariah', 'https://www.cnbcindonesia.com/syariah/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Tech', 'https://www.cnbcindonesia.com/tech/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Lifestyle', 'https://www.cnbcindonesia.com/lifestyle/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Opini', 'https://www.cnbcindonesia.com/opini/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - My Money', 'https://www.cnbcindonesia.com/mymoney/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Cuap Cuap Cuan', 'https://www.cnbcindonesia.com/cuap-cuap-cuan/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Research', 'https://www.cnbcindonesia.com/research/rss');

CREATE TABLE raw_rss_data
(
    id             SERIAL PRIMARY KEY,                                         -- A unique identifier for each record
    news_source_id INT REFERENCES news_sources (id),
    status         VARCHAR(255)                      DEFAULT 'success',        -- Can be 'success', 'error', etc., to indicate fetch status
    error_message  TEXT                              DEFAULT '',               -- In case there was an error fetching or processing the feed, store the error message
    rss_content    TEXT                     NOT NULL,                          -- The full raw content of the RSS feed
    content_hash   VARCHAR(64),                                                -- A hash of the content; can be used to quickly detect if content has changed since last fetch
    collected_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP -- When the RSS data was fetched
);

CREATE INDEX idx_source_url ON raw_rss_data (news_source_id);
CREATE INDEX idx_content_hash ON raw_rss_data (content_hash);

CREATE TABLE news_items
(
    id              SERIAL PRIMARY KEY,
    raw_rss_data_id INT REFERENCES raw_rss_data (id),
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
