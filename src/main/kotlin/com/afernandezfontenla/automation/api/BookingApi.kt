package com.afernandezfontenla.automation.api

import com.afernandezfontenla.automation.client.ApiClient
import com.afernandezfontenla.automation.client.ApiResponse
import com.afernandezfontenla.automation.dto.Booking
import com.afernandezfontenla.automation.dto.PartialBookingUpdate

/**
 * Wraps the /booking resource. Tests talk to this class, never to raw URLs.
 *
 * Write operations are authenticated with the `token` cookie. Pass `token = null`
 * (or an invalid one) to exercise the unauthorized paths.
 */
class BookingApi(
    private val client: ApiClient,
    private val defaultToken: () -> String,
) {

    suspend fun list(firstname: String? = null, lastname: String? = null): ApiResponse {
        val query = buildMap<String, String> {
            firstname?.let { put("firstname", it) }
            lastname?.let { put("lastname", it) }
        }
        return client.get("/booking", query)
    }

    suspend fun get(id: Int): ApiResponse = client.get("/booking/$id")

    suspend fun create(booking: Booking): ApiResponse = client.post("/booking", booking)

    /** Sends an arbitrary payload to POST /booking (used for invalid-body tests). */
    suspend inline fun <reified B> createRaw(payload: B): ApiResponse = rawClient().post("/booking", payload)

    suspend fun update(id: Int, booking: Booking, token: String? = defaultToken()): ApiResponse =
        client.put("/booking/$id", booking, authHeaders(token))

    suspend fun partialUpdate(id: Int, update: PartialBookingUpdate, token: String? = defaultToken()): ApiResponse =
        client.patch("/booking/$id", update, authHeaders(token))

    suspend fun delete(id: Int, token: String? = defaultToken()): ApiResponse =
        client.delete("/booking/$id", authHeaders(token))

    @PublishedApi
    internal fun rawClient(): ApiClient = client

    private fun authHeaders(token: String?): Map<String, String> =
        if (token == null) emptyMap() else mapOf("Cookie" to "token=$token")
}
