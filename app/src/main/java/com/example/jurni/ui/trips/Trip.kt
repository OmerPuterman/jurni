package com.example.jurni.ui.trips

data class Trip(
    val tripId: String = "",
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val flightCost: Double = 0.0,
    val hotelCost: Double = 0.0,
    val totalBudget: Double = 0.0
)
