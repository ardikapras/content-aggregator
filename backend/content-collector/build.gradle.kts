val ktorVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project
val logbackVersion: String by project
val quartzVersion: String by project
val kotlinxVersion: String by project
val hikariVersion: String by project
val romeVersion: String by project

plugins {
    kotlin("jvm") apply true
    id("io.ktor.plugin") version "2.3.5"
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
    testImplementation("io.ktor:ktor-server-tests")
}
