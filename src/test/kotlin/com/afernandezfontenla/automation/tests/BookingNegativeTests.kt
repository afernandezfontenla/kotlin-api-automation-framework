package com.afernandezfontenla.automation.tests

import com.afernandezfontenla.automation.base.BaseApiTest
import com.afernandezfontenla.automation.base.shouldHaveStatus
import com.afernandezfontenla.automation.data.BookingFactory
import com.afernandezfontenla.automation.dto.PartialBookingUpdate
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

/** Error handling and authorization rules. */
class BookingNegativeTests : BaseApiTest() {

    @Test(description = "Requesting an unknown booking returns 404")
    fun getUnknownBookingReturnsNotFound() = runApi {
        bookingApi.get(999_999_999).shouldHaveStatus(404)
    }

    @Test(description = "Updating without a token is forbidden")
    fun updateWithoutTokenIsForbidden() = runApi {
        val id = createBooking().bookingid

        bookingApi.update(id, BookingFactory.random(), token = null).shouldHaveStatus(403)
    }

    @Test(description = "Updating with an invalid token is forbidden")
    fun updateWithInvalidTokenIsForbidden() = runApi {
        val id = createBooking().bookingid

        bookingApi.update(id, BookingFactory.random(), token = "not-a-real-token").shouldHaveStatus(403)
    }

    @Test(description = "Deleting without a token is forbidden and the booking survives")
    fun deleteWithoutTokenIsForbidden() = runApi {
        val id = createBooking().bookingid

        bookingApi.delete(id, token = null).shouldHaveStatus(403)

        bookingApi.get(id).shouldHaveStatus(200)
    }

    @Test(description = "A booking with missing required fields is rejected")
    fun createWithIncompleteBodyIsRejected() = runApi {
        // Restful-Booker answers 500 here (ideally it would be 400), so we assert "an error".
        val response = bookingApi.createRaw(PartialBookingUpdate(firstname = "OnlyFirstName"))

        assertThat(response.status).isGreaterThanOrEqualTo(400)
    }
}
