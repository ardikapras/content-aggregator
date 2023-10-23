import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project
val hikariVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "com.ardikapras"
version = "0.0.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("shared-lib")
    archiveVersion.set("1.0.0")
    manifest {
        attributes["Implementation-Title"] = "Shared Libs"
        attributes["Implementation-Version"] = archiveVersion.get()
    }
}
