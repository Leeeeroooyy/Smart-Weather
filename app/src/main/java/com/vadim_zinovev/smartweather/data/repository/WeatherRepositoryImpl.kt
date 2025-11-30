package com.vadim_zinovev.smartweather.data.repository

import com.vadim_zinovev.smartweather.BuildConfig
import com.vadim_zinovev.smartweather.data.remote.api.WeatherApi
import com.vadim_zinovev.smartweather.data.remote.dto.toDomainAirQuality
import com.vadim_zinovev.smartweather.data.remote.dto.toDomainWeather
import com.vadim_zinovev.smartweather.domain.model.AirQuality
import com.vadim_zinovev.smartweather.domain.model.Weather
import com.vadim_zinovev.smartweather.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {

    private val apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY

    override suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Weather {
        val response = api.getCurrentWeatherByCoordinates(
            latitude = latitude,
            longitude = longitude,
            units = "metric",
            apiKey = apiKey
        )
        return response.toDomainWeather()
    }

    override suspend fun getCurrentWeatherByCityName(cityName: String): Weather {
        val response = api.getCurrentWeatherByCityName(
            cityName = cityName,
            units = "metric",
            apiKey = apiKey
        )
        return response.toDomainWeather()
    }

    override suspend fun getAirQualityByCoordinates(
        latitude: Double,
        longitude: Double
    ): AirQuality? {
        val response = api.getAirQualityByCoordinates(
            latitude = latitude,
            longitude = longitude,
            apiKey = apiKey
        )
        return response.toDomainAirQuality()
    }
}
