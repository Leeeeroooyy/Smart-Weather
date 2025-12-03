package com.vadim_zinovev.smartweather.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vadim_zinovev.smartweather.domain.model.TemperatureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_DATASTORE = "settings"

private val Context.dataStore by preferencesDataStore(name = SETTINGS_DATASTORE)

class SettingsPreferencesDataSource(
    private val context: Context
) {

    private val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")

    val temperatureUnitFlow: Flow<TemperatureUnit> =
        context.dataStore.data.map { prefs ->
            when (prefs[TEMPERATURE_UNIT_KEY]) {
                "FAHRENHEIT" -> TemperatureUnit.FAHRENHEIT
                else -> TemperatureUnit.CELSIUS
            }
        }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { prefs ->
            prefs[TEMPERATURE_UNIT_KEY] = unit.name
        }
    }
}
