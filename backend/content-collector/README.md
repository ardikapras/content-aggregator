# backend:content-collector

## Local Environment

- Run `docker compose up -d` from folder [docker-environment](../../docker-environment)
- Run `gradlew backend:content-collector:run`

## API

- `/v1/content-collector/trigger` - to trigger the job manually
- `/v1/content-collector/start` - to trigger the job automatically
- `/v1/content-collector/stop` - to stop the job
