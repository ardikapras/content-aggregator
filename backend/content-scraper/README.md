# Content Scraper Service

The content scraper is a Spring Boot application that collects, extracts, and processes news articles from various sources using RSS feeds.

## Features

- **Automated Content Collection**: Periodically fetches articles from RSS feeds
- **Content Extraction**: Extracts and cleans article content using source-specific parsing strategies
- **Distributed Processing**: Uses Kafka for message-based article processing
- **Flexible Architecture**: Easily extensible with new content sources and parsing strategies
- **REST API**: Provides a comprehensive API for managing sources and triggering operations

## Technology Stack

- **Core**: Spring Boot 3.4.3, Kotlin 2.1.10
- **Database**: PostgreSQL with JPA/Hibernate
- **Message Broker**: Kafka
- **Web Scraping**: JSoup
- **Concurrency**: Kotlin Coroutines
- **Testing**: JUnit 5, MockK

## Architecture

The service follows a modular architecture with key components:

### Source Management

Configurable news sources with different parsing strategies:
- `Source` - Entity containing RSS feed URL and metadata
- `SourceRepository` - Data access layer for sources
- `SourceController` - REST endpoints for source management

### Content Collection

Components dedicated to fetching and processing RSS feeds:
- `CollectorService` - Fetches RSS feeds and extracts new articles
- `ScraperController` - REST endpoints to trigger collection

### Content Extraction

Content extraction with strategy pattern implementation:
- `NewsParsingStrategy` - Interface for content extraction strategies
- `BaseNewsParsingStrategy` - Common parsing functionality
- `Source-specific strategies` - Custom parsing for different news outlets
- `NewsStrategyManager` - Factory to get appropriate parsing strategy

### Processing Flow

1. `CollectorService` fetches new articles from RSS feeds
2. New articles are saved to database and sent to Kafka
3. `ScraperService` consumes from Kafka and extracts full content
4. Article status is updated throughout the process

## Setup and Running

### Prerequisites

- JDK 21+
- PostgreSQL 14+
- Kafka 3.5+
- Gradle 8+

### Configuration

The application uses Spring configuration. Key properties in `application.yaml`:

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/newsdb}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:postgres}
  kafka:
    bootstrap-servers: ${SPRING_KAFKA_BOOTSTRAP_SERVERS:localhost:9092}

app:
  max-retry-count: ${APP_MAX_RETRY_COUNT:3}
```

### Running Locally

Start with Gradle:

```bash
./gradlew backend:content-scraper:run
```

Or package and run the JAR:

```bash
./gradlew backend:content-scraper:build
java -jar backend/content-scraper/build/libs/content-scraper-0.0.1-SNAPSHOT.jar
```

### Docker

Build and run with Docker:

```bash
docker build -t content-scraper -f backend/content-scraper/Dockerfile .
docker run -p 8080:8080 content-scraper
```

## API Endpoints

### Scraper Operations

- `POST /api/scraper/run` - Trigger content scraping for all active sources
- `POST /api/scraper/re-run` - Retry processing pending articles

### Source Management

- `GET /api/sources` - Get all configured sources
- `GET /api/sources/{id}` - Get a specific source
- `GET /api/sources/active` - Get all active sources
- `POST /api/sources` - Add a new source
- `PUT /api/sources/{id}` - Update a source
- `PUT /api/sources/{id}/toggle-active` - Toggle a source's active status

### Article Access

- `GET /api/articles` - Get articles with pagination and sorting
- `GET /api/articles/{id}` - Get a specific article
- `GET /api/articles/source/{sourceId}` - Get articles for a specific source

## Adding a New Source

1. Add the source details to `database/init/00001-initial-data.refdata.sql`
2. For sources requiring custom parsing:
    - Add a new value to `ParsingStrategy` enum
    - Create a new implementation of `BaseNewsParsingStrategy`
    - Register the strategy in `NewsStrategyManager`

## Development

### Build and Test

```bash
./gradlew backend:content-scraper:build
./gradlew backend:content-scraper:test
```

### Code Style

The project uses ktlint for Kotlin code style checks:

```bash
./gradlew backend:content-scraper:ktlintCheck
```
