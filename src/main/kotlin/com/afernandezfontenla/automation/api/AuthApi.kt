package com.afernandezfontenla.automation.api

import com.afernandezfontenla.automation.client.ApiClient
import com.afernandezfontenla.automation.client.ApiResponse
import com.afernandezfontenla.automation.dto.AuthRequest
import com.afernandezfontenla.automation.dto.AuthResponse

/** Wraps the /auth endpoint. */
class AuthApi(private val client: ApiClient) {

    suspend fun login(request: AuthRequest): ApiResponse = client.post("/auth", request)

    /** Convenience for test setup: returns a valid token or fails loudly. */
    suspend fun createToken(username: String, password: String): String {
        val response = login(AuthRequest(username, password))
        check(response.status == 200) { "Auth failed with HTTP ${response.status}" }
        return response.bodyAs<AuthResponse>().token
            ?: error("Auth response did not contain a token (bad credentials?)")
    }
}
