package com.vadim_zinovev.smartweather.domain.repository

import com.vadim_zinovev.smartweather.domain.model.TemperatureUnit
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeTemperatureUnit(): Flow<TemperatureUnit>
    suspend fun setTemperatureUnit(unit: TemperatureUnit)
}