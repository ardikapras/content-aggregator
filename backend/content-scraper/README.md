# Content Aggregator

Build to aggregate news from different sources and consolidate into apps.

## Build status

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/sample-user/sample-project/tree/master.svg?style=svg&circle-token=sample-token)](https://dl.circleci.com/status-badge/redirect/gh/sample-user/sample-project/tree/master)

## Sonar quality gate status
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sample-project&metric=alert_status&token=sample-token)](https://sonarcloud.io/summary/new_code?id=sample-project)

## Quick Start

### Requirements
- Docker and Docker Compose

### Running with Docker Compose

The easiest way to get started is using Docker Compose:

```bash
# Clone the repository
git clone https://github.com/your-org/content-aggregator.git
cd content-aggregator

# Start all services
docker compose up -d
```

The services will be available at:
- Content Scraper API: http://localhost:8080/api/scraper
- PostgreSQL: localhost:5432 (credentials: postgres/postgres)
- Kafka: localhost:9092

### API Endpoints

- `POST /api/scraper/run` - Trigger scraping for all active news sources
- `POST /api/scraper/re-run` - Retry processing pending articles

## Development Setup

### Backend

Main backend gradle project is [backend](./backend), composed of the following subprojects:

- [content-scraper](./backend/content-scraper) - Service to scrape news from different sources.

#### Local Development

For local development without Docker:

1. Create a PostgreSQL database:

```bash
docker volume create contentdata
docker compose up -d postgres kafka
```

2. Run the application:

```bash
./gradlew backend:content-scraper:run
```

### Frontend

- [main-frontend](./frontend/main-app/README.md) - Main frontend web app. Run locally by executing command `./gradlew frontend:main-app:runDev`.

## Project Structure

```
content-aggregator/
├── backend/
│   └── content-scraper/    # News scraping service
├── frontend/
│   └── main-app/           # Frontend application
├── database/
│   └── init/               # Database initialization scripts
├── docker-compose.yml      # Docker Compose configuration
└── README.md               # This file
```

## Architecture

The system comprises:
- A content scraper that collects articles from various news sources
- A PostgreSQL database for storing content
- A Kafka message broker for handling processing tasks
- A frontend web application (in development)
