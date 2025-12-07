package com.vadim_zinovev.smartweather.domain.model

data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double?,
    val maxTemperature: Double?,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double
)
