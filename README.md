# Content Aggregator

A modern news content aggregation platform built with Spring Boot and React that collects, processes, and displays articles from various online sources.

## Overview

Content Aggregator is a comprehensive platform designed to automatically collect news and articles from various web sources using RSS feeds. It processes and enriches this content to provide a unified reading experience.

### Key Features

- **Automated Content Collection**: Scrapes content from 30+ news sources via RSS feeds
- **Intelligent Processing**: Parses and extracts article content using source-specific strategies
- **Content Enrichment**: Analyzes sentiment, reading time, and other metrics (TODO)
- **Modern UI**: Clean interface for browsing articles from different sources
- **Extensible Architecture**: Easy to add new content sources and processing strategies

## Project Structure

```
content-aggregator/
├── backend/
│   └── content-scraper/    # Spring Boot service for collecting news articles
├── frontend/
│   └── main-app/           # React frontend application
├── database/
│   └── init/               # Database initialization scripts
└── docker-compose.yml      # Docker Compose configuration
```

## Getting Started

### Prerequisites

- JDK 21+
- Node.js 18+
- Docker and Docker Compose
- Gradle 8+

### Quick Start with Docker Compose

The easiest way to run the complete system is with Docker Compose:

```bash
# Clone the repository
git clone https://github.com/your-org/content-aggregator.git
cd content-aggregator

# Start all services
docker compose up -d
```

Services will be available at:
- Frontend App: http://localhost:3000
- Backend API: http://localhost:8080/api
- PostgreSQL: localhost:5432
- Kafka: localhost:9092

### Local Development Setup

#### Backend

1. Start the required infrastructure:
```bash
docker compose up -d
```

2. Run the backend service:
```bash
./gradlew backend:content-scraper:run
```

Or just use the Gradle task:
```bash
./gradlew runBackend
```

#### Frontend

```bash
cd frontend/main-app
npm install
npm run dev
```

Or use the Gradle task:
```bash
./gradlew frontend:main-app:runDev
```

## API Endpoints

### Scraper API

- `POST /api/scraper/run` - Trigger content scraping for all active sources
- `POST /api/scraper/re-run` - Retry processing pending articles

### Article API

- `GET /api/articles` - Get paginated articles
- `GET /api/articles/{id}` - Get article by ID
- `GET /api/articles/source/{sourceId}` - Get articles from a specific source

### Source API

- `GET /api/sources` - Get all sources
- `GET /api/sources/active` - Get active sources
- `POST /api/sources` - Create a new source
- `PUT /api/sources/{id}` - Update a source
- `PUT /api/sources/{id}/toggle-active` - Toggle source active status

## Architecture

### Backend

The backend is built with Spring Boot and Kotlin, following a modular architecture:

- **News Source Management**: Configure and manage different news sources
- **Content Collection**: Fetch and extract content from RSS feeds
- **Content Processing**: Extract and process article content
- **REST API**: Expose content through a RESTful API

### Frontend

The frontend is built with React 19 and Bootstrap, utilizing:

- React Router for navigation
- React Bootstrap for UI components
- Axios for API communication

### Data Flow

1. Configured news sources are read from the database
2. RSS feeds are fetched and new articles are extracted
3. Article URLs are sent to Kafka for scraping
4. Content scraper extracts full article content
5. Articles are stored in the database
6. Frontend retrieves and displays articles via REST API

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
