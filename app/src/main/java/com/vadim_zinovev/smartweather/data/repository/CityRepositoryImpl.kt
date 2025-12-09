package com.vadim_zinovev.smartweather.data.repository

import com.vadim_zinovev.smartweather.BuildConfig
import com.vadim_zinovev.smartweather.data.remote.api.WeatherApi
import com.vadim_zinovev.smartweather.data.remote.dto.toDomainCity
import com.vadim_zinovev.smartweather.domain.model.City
import com.vadim_zinovev.smartweather.domain.repository.CityRepository

class CityRepositoryImpl(
    private val api: WeatherApi
) : CityRepository {

    private val apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY

    override suspend fun searchCitiesByName(query: String): List<City> {
        val result = api.searchCitiesByName(
            query = query,
            limit = 5,
            apiKey = apiKey
        )
        return result.map { it.toDomainCity() }
    }
}
