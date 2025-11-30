package com.vadim_zinovev.smartweather.data.remote.dto

import com.vadim_zinovev.smartweather.domain.model.AirQuality
import com.vadim_zinovev.smartweather.domain.model.City
import com.vadim_zinovev.smartweather.domain.model.Weather

fun CurrentWeatherResponseDto.toDomainWeather(): Weather {
    val firstWeather = weather.firstOrNull()
    return Weather(
        temperature = main.temperature,
        feelsLike = main.feelsLike,
        minTemperature = main.tempMin,
        maxTemperature = main.tempMax,
        description = firstWeather?.description ?: "",
        iconCode = firstWeather?.icon ?: "",
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        timestamp = timestamp
    )
}

fun AirQualityResponseDto.toDomainAirQuality(): AirQuality? {
    val first = list.firstOrNull() ?: return null
    return AirQuality(
        aqi = first.main.aqi,
        pm25 = first.components.pm25,
        pm10 = first.components.pm10
    )
}

fun CityGeocodingDto.toDomainCity(
    id: Long = 0L,        // потом заменить на id из БД
    isFavorite: Boolean = false
): City = City(
    id = id,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
    isFavorite = isFavorite
)
