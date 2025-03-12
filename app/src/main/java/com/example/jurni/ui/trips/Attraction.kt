package com.example.jurni.ui.trips

import java.io.Serializable

data class Attraction(
    val name: String = "",
    val cost: Double = 0.0
) : Serializable