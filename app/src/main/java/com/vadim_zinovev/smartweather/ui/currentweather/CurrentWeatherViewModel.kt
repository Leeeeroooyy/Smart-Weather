package com.vadim_zinovev.smartweather.ui.currentweather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim_zinovev.smartweather.data.remote.api.WeatherApiFactory
import com.vadim_zinovev.smartweather.data.repository.WeatherRepositoryImpl
import com.vadim_zinovev.smartweather.domain.repository.WeatherRepository
import kotlinx.coroutines.launch

class CurrentWeatherViewModel : ViewModel() {


    private val weatherRepository: WeatherRepository =
        WeatherRepositoryImpl(WeatherApiFactory.create())

    var uiState by mutableStateOf(CurrentWeatherUiState())
        private set

    init {
        loadWeatherForCity("Zlin")
    }

    fun loadWeatherForCity(cityName: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null
            )
            try {
                val weather = weatherRepository.getCurrentWeatherByCityName(cityName)
                uiState = CurrentWeatherUiState(
                    isLoading = false,
                    cityName = cityName,
                    temperatureText = "${weather.temperature.toInt()}°C",
                    description = weather.description,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = CurrentWeatherUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Nepodařilo se načíst počasí"
                )
            }
        }
    }
}
