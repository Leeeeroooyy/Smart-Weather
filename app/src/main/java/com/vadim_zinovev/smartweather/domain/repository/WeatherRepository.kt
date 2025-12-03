package com.vadim_zinovev.smartweather.domain.repository

import com.vadim_zinovev.smartweather.domain.model.AirQuality
import com.vadim_zinovev.smartweather.domain.model.TemperatureUnit
import com.vadim_zinovev.smartweather.domain.model.Weather

interface WeatherRepository {

    suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        unit: TemperatureUnit
    ): Weather

    suspend fun getCurrentWeatherByCityName(
        cityName: String,
        unit: TemperatureUnit
    ): Weather

    suspend fun getAirQualityByCoordinates(
        latitude: Double,
        longitude: Double
    ): AirQuality?
}
