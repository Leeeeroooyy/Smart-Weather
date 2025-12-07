package com.vadim_zinovev.smartweather.ui.currentweather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim_zinovev.smartweather.data.location.LocationProvider
import com.vadim_zinovev.smartweather.domain.model.TemperatureUnit
import com.vadim_zinovev.smartweather.domain.repository.SettingsRepository
import com.vadim_zinovev.smartweather.domain.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    var uiState by mutableStateOf(CurrentWeatherUiState())
        private set

    private var currentUnit: TemperatureUnit = TemperatureUnit.CELSIUS
    private var reloadJob: Job? = null

    private enum class Source { NONE, CITY_NAME, COORDINATES }

    private var lastSource: Source = Source.NONE
    private var lastCityName: String? = null
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    init {
        viewModelScope.launch {
            settingsRepository.observeTemperatureUnit().collect { unit ->
                currentUnit = unit
                if (lastSource != Source.NONE && uiState.temperatureText != null) {
                    reloadWithLastSource(showLoader = true)
                }
            }
        }

        loadWeatherForCityCoordinates(
            cityName = "Prague",
            latitude = 50.0755,
            longitude = 14.4378
        )
    }

    fun loadWeatherForCity(cityName: String) {
        lastSource = Source.CITY_NAME
        lastCityName = cityName
        lastLat = null
        lastLon = null
        reloadWithLastSource(showLoader = true)
    }

    fun loadWeatherForCityCoordinates(
        cityName: String,
        latitude: Double,
        longitude: Double
    ) {
        lastSource = Source.COORDINATES
        lastCityName = cityName
        lastLat = latitude
        lastLon = longitude
        reloadWithLastSource(showLoader = true)
    }

    fun loadWeatherForCurrentLocation() {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val location = locationProvider.getCurrentLocation()
                    ?: throw IllegalStateException("Location not available")

                lastSource = Source.COORDINATES
                lastCityName = "My location"
                lastLat = location.latitude
                lastLon = location.longitude

                reloadWeatherByCoordinates(
                    cityName = "My location",
                    latitude = location.latitude,
                    longitude = location.longitude,
                    showLoader = false
                )
            } catch (e: SecurityException) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Location permission is required"
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load location weather"
                )
            }
        }
    }

    private fun reloadWithLastSource(showLoader: Boolean) {
        when (lastSource) {
            Source.CITY_NAME -> {
                val city = lastCityName ?: return
                reloadWeatherByCityName(city, showLoader)
            }

            Source.COORDINATES -> {
                val city = lastCityName ?: return
                val lat = lastLat ?: return
                val lon = lastLon ?: return
                reloadWeatherByCoordinates(city, lat, lon, showLoader)
            }

            Source.NONE -> Unit
        }
    }

    private fun reloadWeatherByCityName(
        city: String,
        showLoader: Boolean
    ) {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            if (showLoader) {
                uiState = uiState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val weather = weatherRepository.getCurrentWeatherByCityName(
                    cityName = city,
                    unit = currentUnit
                )

                val airQuality = weatherRepository.getAirQualityByCoordinates(
                    latitude = weather.latitude,
                    longitude = weather.longitude
                )

                val unitSymbol =
                    if (currentUnit == TemperatureUnit.CELSIUS) "°C" else "°F"

                uiState = uiState.copy(
                    isLoading = false,
                    cityName = city,
                    temperatureText = "${weather.temperature.toInt()}$unitSymbol",
                    description = weather.description,
                    airQualityIndex = airQuality?.aqi,
                    airQualityText = airQuality?.aqi?.let { indexToText(it) },
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load weather"
                )
            }
        }
    }

    private fun reloadWeatherByCoordinates(
        cityName: String,
        latitude: Double,
        longitude: Double,
        showLoader: Boolean
    ) {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            if (showLoader) {
                uiState = uiState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val weather = weatherRepository.getCurrentWeatherByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    unit = currentUnit
                )

                val airQuality = weatherRepository.getAirQualityByCoordinates(
                    latitude = latitude,
                    longitude = longitude
                )

                val unitSymbol =
                    if (currentUnit == TemperatureUnit.CELSIUS) "°C" else "°F"

                uiState = uiState.copy(
                    isLoading = false,
                    cityName = cityName,
                    temperatureText = "${weather.temperature.toInt()}$unitSymbol",
                    description = weather.description,
                    airQualityIndex = airQuality?.aqi,
                    airQualityText = airQuality?.aqi?.let { indexToText(it) },
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load weather"
                )
            }
        }
    }

    private fun indexToText(index: Int): String =
        when (index) {
            1 -> "Good"
            2 -> "Fair"
            3 -> "Moderate"
            4 -> "Poor"
            5 -> "Very poor"
            else -> "Unknown"
        }
}
