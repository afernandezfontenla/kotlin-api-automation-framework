package com.afernandezfontenla.automation.client

/** Keeps secrets (passwords, tokens, cookies) out of the logs and caps very long payloads. */
object LogMasker {
    private val sensitiveJsonFields =
        Regex("(\"(?:password|token)\"\\s*:\\s*\")[^\"]*(\")", RegexOption.IGNORE_CASE)
    private val sensitiveHeaders = setOf("authorization", "cookie", "x-api-key")

    fun safeBody(body: String, maxLength: Int = 2_000): String {
        val masked = sensitiveJsonFields.replace(body, "\$1***\$2")
        return if (masked.length > maxLength) masked.take(maxLength) + "... [truncated]" else masked
    }

    fun safeHeader(name: String, value: String): String =
        if (name.lowercase() in sensitiveHeaders) "***" else value
}
