plugins {
    id("com.github.node-gradle.node") version "3.5.1"
}

node {
    version.set("18.16.0")
    npmVersion.set("9.5.1")
    download.set(true)
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("install") {
    args.set(listOf("install"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("build") {
    dependsOn("install")
    args.set(listOf("run", "build"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("runDev") {
    dependsOn("install")
    args.set(listOf("run", "dev"))
}
