FROM gradle:8.6-jdk21 AS build

WORKDIR /app
COPY . .

WORKDIR /app/backend/content-scraper
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/backend/content-scraper/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx1g", "-jar", "app.jar"]
