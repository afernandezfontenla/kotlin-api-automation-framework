package com.afernandezfontenla.automation.client

import kotlinx.serialization.serializer

/** Immutable snapshot of an HTTP response. The body is read once and kept as text. */
data class ApiResponse(
    val status: Int,
    val body: String,
    val headers: Map<String, List<String>>,
    val durationMs: Long,
) {
    /** Deserializes the body into a DTO: `response.bodyAs<Booking>()`. */
    inline fun <reified T> bodyAs(): T = JsonSupport.json.decodeFromString(serializer<T>(), body)
}
