package com.afernandezfontenla.automation.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookingDates(
    val checkin: String,   // ISO date: yyyy-MM-dd
    val checkout: String,
)

@Serializable
data class Booking(
    val firstname: String,
    val lastname: String,
    val totalprice: Int,
    val depositpaid: Boolean,
    val bookingdates: BookingDates,
    val additionalneeds: String? = null,
)

/** Response of POST /booking. */
@Serializable
data class BookingCreatedResponse(
    val bookingid: Int,
    val booking: Booking,
)

/** Item returned by GET /booking (list endpoint). */
@Serializable
data class BookingId(
    val bookingid: Int,
)

/** Payload for PATCH: every field optional, absent fields are not serialized. */
@Serializable
data class PartialBookingUpdate(
    val firstname: String? = null,
    val lastname: String? = null,
    val totalprice: Int? = null,
    val depositpaid: Boolean? = null,
    val bookingdates: BookingDates? = null,
    val additionalneeds: String? = null,
)
