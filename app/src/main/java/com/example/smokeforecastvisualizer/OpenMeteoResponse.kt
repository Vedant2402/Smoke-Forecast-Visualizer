package com.example.smokeforecastvisualizer

data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: Hourly
)

data class Hourly(
    val time: List<String>,
    val temperature_2m: List<Double>
)