package com.vadim_zinovev.smartweather.data.remote.dto

import com.squareup.moshi.Json

data class AirQualityResponseDto(
    val list: List<AirQualityItemDto>
)

data class AirQualityItemDto(
    val main: AirQualityIndexDto,
    val components: AirQualityComponentsDto
)

data class AirQualityIndexDto(
    val aqi: Int
)

data class AirQualityComponentsDto(
    @Json(name = "pm2_5")
    val pm25: Double?,
    @Json(name = "pm10")
    val pm10: Double?
)
