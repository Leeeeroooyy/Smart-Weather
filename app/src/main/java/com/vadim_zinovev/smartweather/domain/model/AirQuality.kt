package com.vadim_zinovev.smartweather.domain.model

data class AirQuality(
    val index: Int,
    val aqi: Int,
    val pm25: Double?,
    val pm10: Double?,
    val o3: Double?
)