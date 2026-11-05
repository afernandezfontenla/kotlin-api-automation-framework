package com.afernandezfontenla.automation.base

import com.afernandezfontenla.automation.client.ApiResponse
import org.assertj.core.api.Assertions.assertThat

/** Asserts the HTTP status and, on failure, prints the body so the reason is visible in the report. */
fun ApiResponse.shouldHaveStatus(expected: Int): ApiResponse {
    assertThat(status)
        .withFailMessage("Expected HTTP %s but was %s. Response body: %s", expected, status, body)
        .isEqualTo(expected)
    return this
}
