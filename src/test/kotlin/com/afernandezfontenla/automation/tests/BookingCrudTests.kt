package com.afernandezfontenla.automation.tests

import com.afernandezfontenla.automation.base.BaseApiTest
import com.afernandezfontenla.automation.base.shouldHaveStatus
import com.afernandezfontenla.automation.data.BookingFactory
import com.afernandezfontenla.automation.dto.Booking
import com.afernandezfontenla.automation.dto.BookingId
import com.afernandezfontenla.automation.dto.PartialBookingUpdate
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

/** Happy-path coverage of the booking resource. Every test creates its own data. */
class BookingCrudTests : BaseApiTest() {

    @Test(description = "POST /booking returns an id and echoes the data we sent")
    fun createBookingReturnsIdAndEchoesData() = runApi {
        val booking = BookingFactory.random()

        val created = createBooking(booking)

        assertThat(created.bookingid).isPositive()
        assertThat(created.booking).isEqualTo(booking)
    }

    @Test(description = "GET /booking/{id} returns the persisted booking")
    fun getBookingByIdReturnsCreatedBooking() = runApi {
        val booking = BookingFactory.random()
        val id = createBooking(booking).bookingid

        val fetched = bookingApi.get(id).shouldHaveStatus(200).bodyAs<Booking>()

        assertThat(fetched).isEqualTo(booking)
    }

    @Test(description = "GET /booking lists the ids of existing bookings")
    fun listContainsCreatedBooking() = runApi {
        val id = createBooking().bookingid

        val ids = bookingApi.list().shouldHaveStatus(200)
            .bodyAs<List<BookingId>>().map { it.bookingid }

        assertThat(ids).contains(id)
    }

    @Test(description = "GET /booking?firstname= filters by first name")
    fun listCanBeFilteredByFirstName() = runApi {
        val firstname = BookingFactory.uniqueName("Filter")
        val id = createBooking(BookingFactory.random(firstname = firstname)).bookingid

        val ids = bookingApi.list(firstname = firstname).shouldHaveStatus(200)
            .bodyAs<List<BookingId>>().map { it.bookingid }

        assertThat(ids).containsExactly(id)
    }

    @Test(description = "PUT /booking/{id} replaces the whole booking")
    fun fullUpdateReplacesBooking() = runApi {
        val id = createBooking().bookingid
        val replacement = BookingFactory.random(lastname = "Replaced")

        val updated = bookingApi.update(id, replacement).shouldHaveStatus(200).bodyAs<Booking>()
        val fetched = bookingApi.get(id).shouldHaveStatus(200).bodyAs<Booking>()

        assertThat(updated).isEqualTo(replacement)
        assertThat(fetched).isEqualTo(replacement)
    }

    @Test(description = "PATCH /booking/{id} changes only the supplied fields")
    fun partialUpdateChangesOnlyGivenFields() = runApi {
        val original = BookingFactory.random()
        val id = createBooking(original).bookingid

        val patched = bookingApi
            .partialUpdate(id, PartialBookingUpdate(firstname = "Patched", totalprice = 999))
            .shouldHaveStatus(200)
            .bodyAs<Booking>()

        assertThat(patched.firstname).isEqualTo("Patched")
        assertThat(patched.totalprice).isEqualTo(999)
        assertThat(patched.lastname).isEqualTo(original.lastname)
        assertThat(patched.bookingdates).isEqualTo(original.bookingdates)
    }

    @Test(description = "DELETE /booking/{id} removes the booking")
    fun deleteRemovesBooking() = runApi {
        val id = createBooking().bookingid

        // Restful-Booker quirk: a successful delete answers 201 Created.
        bookingApi.delete(id).shouldHaveStatus(201)

        bookingApi.get(id).shouldHaveStatus(404)
    }
}
