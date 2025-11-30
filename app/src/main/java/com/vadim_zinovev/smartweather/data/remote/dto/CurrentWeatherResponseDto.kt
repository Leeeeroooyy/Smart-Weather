package com.vadim_zinovev.smartweather.data.remote.dto

import com.squareup.moshi.Json

data class CurrentWeatherResponseDto(
    @Json(name = "dt")
    val timestamp: Long,
    @Json(name = "name")
    val cityName: String?,
    val weather: List<WeatherDescriptionDto>,
    val main: MainInfoDto,
    val wind: WindDto
)

data class WeatherDescriptionDto(
    val description: String,
    val icon: String
)

data class MainInfoDto(
    @Json(name = "temp")
    val temperature: Double,
    @Json(name = "feels_like")
    val feelsLike: Double,
    @Json(name = "temp_min")
    val tempMin: Double?,
    @Json(name = "temp_max")
    val tempMax: Double?,
    val pressure: Int,
    val humidity: Int
)

data class WindDto(
    @Json(name = "speed")
    val speed: Double
)
