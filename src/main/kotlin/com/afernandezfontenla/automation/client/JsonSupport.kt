package com.afernandezfontenla.automation.client

import kotlinx.serialization.json.Json

/** Single shared [Json] configuration used to encode requests and decode responses. */
object JsonSupport {
    val json: Json = Json {
        ignoreUnknownKeys = true   // new fields in the API must not break the tests
        encodeDefaults = false     // null defaults are omitted (needed for PATCH payloads)
    }
}
