package com.vadim_zinovev.smartweather.domain.repository

import com.vadim_zinovev.smartweather.domain.model.City

interface CityRepository {

    suspend fun searchCitiesByName(query: String): List<City>
}
