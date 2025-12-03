package com.vadim_zinovev.smartweather.data.repository

import android.content.Context
import com.vadim_zinovev.smartweather.data.local.SettingsPreferencesDataSource
import com.vadim_zinovev.smartweather.domain.model.TemperatureUnit
import com.vadim_zinovev.smartweather.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    context: Context
) : SettingsRepository {

    private val dataSource = SettingsPreferencesDataSource(context)

    override fun observeTemperatureUnit(): Flow<TemperatureUnit> =
        dataSource.temperatureUnitFlow

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        dataSource.setTemperatureUnit(unit)
    }
}
