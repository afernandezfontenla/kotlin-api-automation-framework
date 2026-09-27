plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.api.automation"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.8"
val testngVersion = "7.9.0"

dependencies {
    // TestNG para el motor de pruebas
    testImplementation("org.testng:testng:$testngVersion")

    // Ktor Client (Core y motor CIO para hacer peticiones HTTP)
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // Ktor ContentNegotiation y Kotlinx Serialization (para manejar JSON fácilmente)
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Kotlin standard library
    implementation(kotlin("stdlib"))
}

tasks.test {
    useTestNG()
}