package com.vadim_zinovev.smartweather.domain.model

data class AirQuality(
    val aqi: Int,
    val pm25: Double?,
    val pm10: Double?
)