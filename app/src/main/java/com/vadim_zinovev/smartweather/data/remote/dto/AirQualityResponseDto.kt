package com.vadim_zinovev.smartweather.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AirQualityResponseDto(
    @Json(name = "list") val list: List<AirQualityItemDto>
)

@JsonClass(generateAdapter = true)
data class AirQualityItemDto(
    @Json(name = "main") val main: AirQualityMainDto,
    @Json(name = "components") val components: AirQualityComponentsDto
)

@JsonClass(generateAdapter = true)
data class AirQualityMainDto(
    @Json(name = "aqi") val aqi: Int
)

@JsonClass(generateAdapter = true)
data class AirQualityComponentsDto(
    @Json(name = "pm2_5") val pm25: Double?,
    @Json(name = "pm10") val pm10: Double?,
    @Json(name = "o3") val o3: Double?
)

