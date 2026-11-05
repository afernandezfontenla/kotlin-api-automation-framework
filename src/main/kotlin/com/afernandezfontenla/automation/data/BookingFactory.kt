package com.afernandezfontenla.automation.data

import com.afernandezfontenla.automation.dto.Booking
import com.afernandezfontenla.automation.dto.BookingDates
import java.time.LocalDate
import kotlin.random.Random

/** Builds valid, unique test data so tests never depend on each other or on pre-existing records. */
object BookingFactory {

    private val needs = listOf("Breakfast", "Late checkout", "Extra bed", "Parking")

    fun uniqueName(prefix: String = "QA"): String = prefix + Random.nextInt(100_000, 999_999)

    fun random(
        firstname: String = uniqueName(),
        lastname: String = "Automation",
        totalprice: Int = Random.nextInt(50, 1_000),
        depositpaid: Boolean = Random.nextBoolean(),
        additionalneeds: String? = needs.random(),
    ): Booking {
        val checkin = LocalDate.now().plusDays(Random.nextLong(1, 30))
        return Booking(
            firstname = firstname,
            lastname = lastname,
            totalprice = totalprice,
            depositpaid = depositpaid,
            bookingdates = BookingDates(checkin.toString(), checkin.plusDays(Random.nextLong(1, 10)).toString()),
            additionalneeds = additionalneeds,
        )
    }
}
