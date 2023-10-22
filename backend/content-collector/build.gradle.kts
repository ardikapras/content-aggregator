val ktorVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project
val logbackVersion: String by project
val quartzVersion: String by project
val kotlinxVersion: String by project
val hikariVersion: String by project
val romeVersion: String by project
val commonsIoVersion: String by project
val slf4jVersion: String by project
val kafkaVersion: String by project

plugins {
    kotlin("jvm") apply true
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

group = "com.ardikapras"
version = "0.0.1"

application {
    mainClass.set("com.ardikapras.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    implementation("com.rometools:rome:$romeVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    testImplementation("io.ktor:ktor-server-tests")
}
