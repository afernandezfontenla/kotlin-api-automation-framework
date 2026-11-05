import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.afernandezfontenla"
version = "1.0.0"

repositories {
    mavenCentral()
}

val ktorVersion = "3.2.0"

dependencies {
    // HTTP client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // JSON <-> DTOs
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // Logging API (the backend lives in the test classpath)
    implementation("org.slf4j:slf4j-api:2.0.17")

    // Test stack
    testImplementation("org.testng:testng:7.11.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.5.18")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useTestNG()

    // Allow overriding the target environment: ./gradlew test -Dapi.baseUrl=https://my-env.example.com
    listOf("api.baseUrl", "api.username", "api.password").forEach { key ->
        System.getProperty(key)?.let { systemProperty(key, it) }
    }

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}
