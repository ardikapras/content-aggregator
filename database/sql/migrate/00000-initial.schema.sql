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


-- PostgreSQL database dump complete
