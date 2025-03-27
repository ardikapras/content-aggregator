# content-aggregator
Build to aggregate news from different source and consolidate into apps

## Build status

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/sample-user/sample-project/tree/master.svg?style=svg&circle-token=sample-token)](https://dl.circleci.com/status-badge/redirect/gh/sample-user/sample-project/tree/master)

## Sonar quality gate status
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sample-project&metric=alert_status&token=sample-token)](https://sonarcloud.io/summary/new_code?id=sample-project)

## Apps

### Backend

Main backend gradle project is [backend](./backend), composed of the following subprojects

- [content-scraper](./backend/content-scraper) Service to scrape news from different sources. Run locally by execute command `gradlew backend:content-scraper:run`.

### Frontend

- [main-frontend](./frontend/main-app/README.md) Main frontend web app. Run locally by execute command `gradlew frontend:main-app:runDev`.
