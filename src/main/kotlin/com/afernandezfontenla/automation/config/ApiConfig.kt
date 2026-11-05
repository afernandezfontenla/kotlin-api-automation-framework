package com.afernandezfontenla.automation.config

/**
 * Central place for environment configuration.
 *
 * Resolution order for every value: JVM system property -> environment variable -> default.
 * Nothing sensitive should ever be hardcoded here for a real project; the defaults below are
 * the public demo credentials documented by Restful-Booker.
 */
object ApiConfig {
    val baseUrl: String = resolve("api.baseUrl", "API_BASE_URL", "https://restful-booker.herokuapp.com")
    val username: String = resolve("api.username", "API_USERNAME", "admin")
    val password: String = resolve("api.password", "API_PASSWORD", "password123")

    private fun resolve(property: String, envVar: String, default: String): String =
        System.getProperty(property) ?: System.getenv(envVar) ?: default
}
