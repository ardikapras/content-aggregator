# Content Aggregator

A modern news content aggregation platform built with Spring Boot and React that collects, processes, and displays articles from various online sources.

## Overview

Content Aggregator is a comprehensive platform designed to automatically collect news and articles from various web sources using RSS feeds. It processes and enriches this content to provide a unified reading experience.

### Key Features

- **Automated Content Collection**: Scrapes content from 30+ news sources via RSS feeds
- **Intelligent Processing**: Parses and extracts article content using source-specific strategies
- **Interactive Dashboard**: Statistics dashboard with article trends, source health monitoring, and activity logs
- **Parser Configuration**: Dynamic parser configuration management with testing capabilities
- **Advanced Filtering**: Search and filter articles by source, status, date range, and keywords
- **Activity Logging**: Comprehensive logging of scraper activities and system events
- **Modern UI**: Clean, responsive interface built with React 19 and Bootstrap
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
git clone https://github.com/ardikapras/content-aggregator.git
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

- `GET /api/articles` - Get paginated articles with filtering (search, source, status, date range)
- `GET /api/articles/{id}` - Get article by ID
- `GET /api/articles/source/{sourceId}` - Get articles from a specific source

### Source API

- `GET /api/sources` - Get all sources
- `GET /api/sources/{id}` - Get source by ID
- `GET /api/sources/active` - Get active sources
- `POST /api/sources` - Create a new source
- `PUT /api/sources/{id}` - Update a source
- `PUT /api/sources/{id}/toggle-active` - Toggle source active status

### Dashboard API

- `GET /api/dashboard/stats` - Get dashboard overview statistics
- `GET /api/dashboard/trends/scraped` - Get article trends by scrape date
- `GET /api/dashboard/trends/published` - Get article trends by publish date
- `GET /api/dashboard/sources/health` - Get source health status
- `GET /api/dashboard/activities` - Get recent scraper activities
- `GET /api/dashboard/articles/recent` - Get most recent articles

### Parser Configuration API

- `GET /api/parser-configs` - Get all parser configurations
- `GET /api/parser-configs/{id}` - Get parser configuration by ID
- `POST /api/parser-configs` - Create a new parser configuration
- `PUT /api/parser-configs/{id}` - Update a parser configuration
- `DELETE /api/parser-configs/{id}` - Delete a parser configuration
- `POST /api/parser-configs/test` - Test a parser configuration on a URL

### Activity Log API

- `GET /api/activities` - Get recent activities
- `GET /api/activities/date-range` - Get activities by date range
- `GET /api/activities/status` - Get activities by status

## Architecture

### Backend

The backend is built with Spring Boot and Kotlin, following a modular architecture:

- **News Source Management**: Configure and manage different news sources
- **Content Collection**: Fetch and extract content from RSS feeds
- **Content Processing**: Extract and process article content using configurable parsers
- **Dashboard Services**: Statistics computation, trends analysis, and source monitoring
- **Parser Configuration**: Dynamic parser management with testing capabilities
- **Activity Logging**: Comprehensive system activity tracking and reporting
- **REST API**: Expose content and management features through RESTful APIs

### Frontend

The frontend is built with React 19 and Bootstrap, utilizing:

- **React Router** for navigation between different views
- **React Bootstrap** for responsive UI components and layout
- **Custom Hooks** for state management and API integration
- **Chart Components** for data visualization (trends, statistics)
- **Interactive Dashboard** with filtering and search capabilities
- **Parser Configuration UI** with live testing capabilities
- **Axios** for API communication

### Data Flow

1. **Source Configuration**: News sources and parser configurations are managed through the admin interface
2. **Content Discovery**: RSS feeds are fetched and new articles are extracted
3. **Queue Processing**: Article URLs are sent to Kafka for asynchronous scraping
4. **Content Extraction**: Content scraper uses configured parsers to extract full article content
5. **Data Storage**: Articles and activity logs are stored in PostgreSQL database
6. **Dashboard Updates**: Statistics and trends are calculated and displayed on demand
7. **Frontend Display**: Users interact with articles, statistics, and configurations via the React UI

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
