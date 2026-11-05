package com.afernandezfontenla.automation.client

import com.afernandezfontenla.automation.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory

/**
 * Thin, generic HTTP client for API tests.
 *
 * - Never throws on 4xx/5xx: negative tests need to assert on error responses.
 * - Serializes request DTOs and returns an [ApiResponse] the caller can decode into DTOs.
 * - Logs every request/response (with timing) through SLF4J, masking secrets.
 */
class ApiClient(baseUrl: String = ApiConfig.baseUrl) : AutoCloseable {

    private val log = LoggerFactory.getLogger(ApiClient::class.java)
    private val baseUrl = baseUrl.trimEnd('/')

    private val http = HttpClient(CIO) {
        expectSuccess = false
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
    }

    suspend fun get(
        path: String,
        query: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
    ): ApiResponse = execute(HttpMethod.Get, path, null, query, headers)

    suspend fun delete(
        path: String,
        headers: Map<String, String> = emptyMap(),
    ): ApiResponse = execute(HttpMethod.Delete, path, null, emptyMap(), headers)

    suspend inline fun <reified B> post(
        path: String,
        body: B,
        headers: Map<String, String> = emptyMap(),
    ): ApiResponse = execute(HttpMethod.Post, path, encode(body), emptyMap(), headers)

    suspend inline fun <reified B> put(
        path: String,
        body: B,
        headers: Map<String, String> = emptyMap(),
    ): ApiResponse = execute(HttpMethod.Put, path, encode(body), emptyMap(), headers)

    suspend inline fun <reified B> patch(
        path: String,
        body: B,
        headers: Map<String, String> = emptyMap(),
    ): ApiResponse = execute(HttpMethod.Patch, path, encode(body), emptyMap(), headers)

    /** Low-level entry point shared by all verbs. */
    suspend fun execute(
        httpMethod: HttpMethod,
        path: String,
        jsonBody: String? = null,
        query: Map<String, String> = emptyMap(),
        extraHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse {
        val url = baseUrl + "/" + path.trimStart('/')
        val displayUrl = if (query.isEmpty()) url else url + "?" + query.entries.joinToString("&") { "${it.key}=${it.value}" }

        log.info("--> {} {}", httpMethod.value, displayUrl)
        if (extraHeaders.isNotEmpty()) {
            log.debug("    headers: {}", extraHeaders.mapValues { (name, value) -> LogMasker.safeHeader(name, value) })
        }
        if (jsonBody != null) log.debug("    body: {}", LogMasker.safeBody(jsonBody))

        val startNanos = System.nanoTime()
        val response = http.request(url) {
            method = httpMethod
            accept(ContentType.Application.Json)
            query.forEach { (key, value) -> parameter(key, value) }
            extraHeaders.forEach { (name, value) -> header(name, value) }
            if (jsonBody != null) {
                contentType(ContentType.Application.Json)
                setBody(jsonBody)
            }
        }
        val responseBody = response.bodyAsText()
        val durationMs = (System.nanoTime() - startNanos) / 1_000_000

        log.info("<-- {} ({} ms)", response.status.value, durationMs)
        if (responseBody.isNotBlank()) log.debug("    body: {}", LogMasker.safeBody(responseBody))

        return ApiResponse(
            status = response.status.value,
            body = responseBody,
            headers = response.headers.entries().associate { it.key to it.value },
            durationMs = durationMs,
        )
    }

    override fun close() = http.close()

    companion object {
        /** Public because it is used by the public inline verbs above. */
        inline fun <reified B> encode(body: B): String =
            JsonSupport.json.encodeToString(serializer<B>(), body)
    }
}
