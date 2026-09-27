package client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import logger.CustomLogger
import kotlin.system.measureTimeMillis

class ApiClient(private val baseUrl: String) {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun get(endpoint: String): HttpResponse {
        val url = "$baseUrl$endpoint"
        CustomLogger.request("GET", url)

        var response: HttpResponse
        val duration = measureTimeMillis {
            response = client.get(url)
        }

        val responseBody = response.bodyAsText()
        CustomLogger.response(response.status.value, responseBody, duration)
        return response
    }

    suspend fun post(endpoint: String, jsonBody: String): HttpResponse {
        val url = "$baseUrl$endpoint"
        CustomLogger.request("POST", url, jsonBody)

        var response: HttpResponse
        val duration = measureTimeMillis {
            response = client.post(url) {
                setBody(jsonBody)
                header("Content-Type", "application/json")
            }
        }

        val responseBody = response.bodyAsText()
        CustomLogger.response(response.status.value, responseBody, duration)
        return response
    }
}