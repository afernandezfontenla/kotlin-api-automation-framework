package com.afernandezfontenla.automation.base

import com.afernandezfontenla.automation.api.AuthApi
import com.afernandezfontenla.automation.api.BookingApi
import com.afernandezfontenla.automation.client.ApiClient
import com.afernandezfontenla.automation.config.ApiConfig
import com.afernandezfontenla.automation.data.BookingFactory
import com.afernandezfontenla.automation.dto.Booking
import com.afernandezfontenla.automation.dto.BookingCreatedResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass

abstract class BaseApiTest {

    protected lateinit var client: ApiClient
    protected lateinit var authApi: AuthApi
    protected lateinit var bookingApi: BookingApi

    private lateinit var token: String
    private val createdBookingIds = mutableListOf<Int>()

    @BeforeClass(alwaysRun = true)
    fun setUpClient() {
        client = ApiClient()
        authApi = AuthApi(client)
        token = runBlocking { authApi.createToken(ApiConfig.username, ApiConfig.password) }
        bookingApi = BookingApi(client) { token }
    }

    /** Best-effort cleanup of every booking created through [createBooking]. */
    @AfterClass(alwaysRun = true)
    fun tearDownClient() {
        if (!::client.isInitialized) return
        runBlocking {
            createdBookingIds.forEach { id -> runCatching { bookingApi.delete(id) } }
        }
        client.close()
    }

    /**
     * Runs a suspend test body. It returns Unit on purpose: TestNG ignores @Test methods
     * that return a value, so every test must compile to a `void` method.
     */
    protected fun runApi(block: suspend CoroutineScope.() -> Unit): Unit = runBlocking(block = block)

    /** Creates a booking through the API, registers it for cleanup and returns the response. */
    protected suspend fun createBooking(booking: Booking = BookingFactory.random()): BookingCreatedResponse {
        val response = bookingApi.create(booking).shouldHaveStatus(200)
        return response.bodyAs<BookingCreatedResponse>().also { createdBookingIds += it.bookingid }
    }
}
