package com.vadim_zinovev.smartweather.data.remote.dto

import com.squareup.moshi.Json

data class CurrentWeatherResponseDto(
    @Json(name = "coord") val coord: CoordDto,
    @Json(name = "weather") val weather: List<WeatherDescriptionDto>,
    @Json(name = "main") val main: MainDto,
    @Json(name = "wind") val wind: WindDto,
    @Json(name = "dt") val timestamp: Long,
    @Json(name = "name") val name: String
)

data class CoordDto(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double
)

data class MainDto(
    @Json(name = "temp") val temperature: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double?,
    @Json(name = "temp_max") val tempMax: Double?,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "pressure") val pressure: Int
)

data class WindDto(
    @Json(name = "speed") val speed: Double
)

data class WeatherDescriptionDto(
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)