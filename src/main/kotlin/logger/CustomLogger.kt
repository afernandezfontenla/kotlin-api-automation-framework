package logger

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CustomLogger {

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    private fun timestamp(): String = LocalTime.now().format(timeFormatter)

    fun info(message: String) {
        println("\u001B[34m[INFO] [${timestamp()}] - $message\u001B[0m")
    }

    fun success(message: String) {
        println("\u001B[32m[SUCCESS] [${timestamp()}] - $message\u001B[0m")
    }

    fun error(message: String) {
        println("\u001B[31m[ERROR] [${timestamp()}] - $message\u001B[0m")
    }

    fun request(method: String, url: String, body: String? = null) {
        println("\u001B[33m--------------------------------------------------\u001B[0m")
        println("\u001B[33m[REQUEST] [${timestamp()}] $method -> $url\u001B[0m")
        if (!body.isNullOrBlank()) {
            println("\u001B[33m[BODY OUT] $body\u001B[0m")
        }
    }

    fun response(statusCode: Int, body: String? = null, durationMs: Long) {
        val color = if (statusCode in 200..299) "\u001B[32m" else "\u001B[31m"
        println("$color[RESPONSE] [${timestamp()}] Status: $statusCode | Time: ${durationMs}ms\u001B[0m")
        if (!body.isNullOrBlank()) {
            println("$color[BODY IN] $body\u001B[0m")
        }
        println("\u001B[33m--------------------------------------------------\u001B[0m")
    }
}