plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    kotlin("plugin.jpa") version "2.1.10"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

group = "io.content-scraper"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

object Versions {
    const val COROUTINES = "1.10.2"
    const val JSOUP = "1.21.2"
    const val POSTGRESQL = "42.7.8"
    const val KOTLIN_LOGGING = "7.0.13"
    const val IO_MOCKK = "1.14.5"
    const val SPRING_MOCK = "4.0.2"
    const val KOTLIN = "2.1.10"
    const val JACKSON = "2.18.3"
    const val SPRING_RETRY = "2.0.11"
    const val ROME = "2.1.0"
    const val TEMPORAL = "1.31.0"
    const val MICROMETER = "1.16.0-M3"
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Observability - Metrics (Prometheus)
    implementation("io.micrometer:micrometer-registry-prometheus:${Versions.MICROMETER}")
    implementation("io.micrometer:micrometer-core:${Versions.MICROMETER}")

    // Observability - Distributed Tracing (Zipkin)
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // Temporal
    implementation("io.temporal:temporal-opentracing:${Versions.TEMPORAL}")
    implementation("io.temporal:temporal-kotlin:${Versions.TEMPORAL}")
    implementation("io.temporal:temporal-spring-boot-starter:${Versions.TEMPORAL}")

    // XML parser
    implementation("com.rometools:rome:${Versions.ROME}")

    // Kotlin
//    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}")
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.COROUTINES}")

    // Web Scraping
    implementation("org.jsoup:jsoup:${Versions.JSOUP}")

    // Database
    runtimeOnly("org.postgresql:postgresql:${Versions.POSTGRESQL}")

    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING}")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:${Versions.IO_MOCKK}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}")
    testImplementation("com.ninja-squad:springmockk:${Versions.SPRING_MOCK}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Retryable
    implementation("org.springframework.retry:spring-retry:${Versions.SPRING_RETRY}")
    implementation("org.springframework.boot:spring-boot-starter-aop")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configure noArg plugin for JPA entities
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// Configuration for running the application
tasks.bootRun {
    jvmArgs = listOf("-Xms256m", "-Xmx1g")
}

ktlint {
    version.set("1.5.0")
}
