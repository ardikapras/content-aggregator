--liquibase formatted sql

--changeset ardikapras:schema-1 logicalFilePath:path-independent

CREATE SCHEMA IF NOT EXISTS liquibase;

ALTER SCHEMA liquibase OWNER TO adminuser;
