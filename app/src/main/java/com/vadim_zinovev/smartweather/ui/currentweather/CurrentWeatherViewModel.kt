package com.vadim_zinovev.smartweather.ui.currentweather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim_zinovev.smartweather.domain.model.TemperatureUnit
import com.vadim_zinovev.smartweather.domain.repository.SettingsRepository
import com.vadim_zinovev.smartweather.domain.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var uiState by mutableStateOf(CurrentWeatherUiState())
        private set

    private var currentUnit: TemperatureUnit = TemperatureUnit.CELSIUS
    private var reloadJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.observeTemperatureUnit().collect { unit ->
                currentUnit = unit
                val city = uiState.cityName ?: "Zlin"
                val isFirstLoad = uiState.temperatureText == null
                reloadWeather(city, showLoader = isFirstLoad)
            }
        }
    }

    fun loadWeatherForCity(cityName: String) {
        reloadWeather(cityName, showLoader = true)
    }

    private fun reloadWeather(city: String, showLoader: Boolean) {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            if (showLoader) {
                uiState = uiState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            try {
                val weather = weatherRepository.getCurrentWeatherByCityName(city, currentUnit)
                val unitSymbol = if (currentUnit == TemperatureUnit.CELSIUS) "°C" else "°F"

                uiState = CurrentWeatherUiState(
                    isLoading = false,
                    cityName = city,
                    temperatureText = "${weather.temperature.toInt()}$unitSymbol",
                    description = weather.description,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Nepodařilo se načíst počasí"
                )
            }
        }
    }
}
