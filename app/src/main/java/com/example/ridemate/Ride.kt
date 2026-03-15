package com.example.ridemate

data class Ride(
    val id: String = "",
    val driverId: String = "",
    val driverName: String = "",
    val destination: String = "",
    val time: String = "",
    val seats: Int = 0,
    val vehicle: String = "Not specified",
    val status: String = "active"
)