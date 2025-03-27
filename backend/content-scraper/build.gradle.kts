plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    kotlin("plugin.jpa") version "2.1.10"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

group = "io.news"
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
    const val COROUTINES = "1.10.1"
    const val JSOUP = "1.19.1"
    const val POSTGRESQL = "42.7.5"
    const val H2DB = "2.3.232"
    const val KOTLIN_LOGGING = "7.0.5"
    const val IO_MOCKK = "1.13.17"
    const val SPRING_MOCK = "4.0.2"
    const val KOTLIN = "2.1.10"
    const val JACKSON = "2.18.3"
    const val SPRING_RETRY = "2.0.11"
    const val KOTLIN_SERIALIZATION = "1.8.0"
    const val GCP_POSTGRESQL = "1.24.0"
    const val ROME = "2.1.0"
    const val SPRING_KAFKA = "3.3.4"
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // kafka
    implementation("org.springframework.kafka:spring-kafka:${Versions.SPRING_KAFKA}")

    // XML parser
    implementation("com.rometools:rome:${Versions.ROME}")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.COROUTINES}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLIN_SERIALIZATION}")

    // Web Scraping
    implementation("org.jsoup:jsoup:${Versions.JSOUP}")

    // Database
    runtimeOnly("org.postgresql:postgresql:${Versions.POSTGRESQL}")

    // Logging
    implementation("io.github.oshai:kotlin-logging:${Versions.KOTLIN_LOGGING}")

    implementation("com.google.cloud.sql:postgres-socket-factory:${Versions.GCP_POSTGRESQL}")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:${Versions.IO_MOCKK}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}")
    testImplementation("com.ninja-squad:springmockk:${Versions.SPRING_MOCK}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

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
