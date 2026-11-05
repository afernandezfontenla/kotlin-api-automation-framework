package com.afernandezfontenla.automation.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username: String,
    val password: String,
)

/** On success `token` is set; on bad credentials the API answers 200 with `reason` instead. */
@Serializable
data class AuthResponse(
    val token: String? = null,
    val reason: String? = null,
)
