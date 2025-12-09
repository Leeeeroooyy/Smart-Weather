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

        loadWeatherForCurrentLocation()
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
        lastCityName = cityName        // метка на случай, если name из API пустой
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

                val latitude = location.latitude
                val longitude = location.longitude

                lastSource = Source.COORDINATES
                lastLat = latitude
                lastLon = longitude
                lastCityName = null

                reloadWeatherByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    showLoader = false
                )
            } catch (e: SecurityException) {
                loadWeatherForCity("Prague")
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
                val lat = lastLat ?: return
                val lon = lastLon ?: return
                reloadWeatherByCoordinates(lat, lon, showLoader)
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

                val feelsLikeText = "${weather.feelsLike.toInt()}$unitSymbol"
                val minTempText = weather.minTemperature?.let { "${it.toInt()}$unitSymbol" }
                val maxTempText = weather.maxTemperature?.let { "${it.toInt()}$unitSymbol" }
                val windSpeedText = "${weather.windSpeed} m/s"

                val forecast = buildDailyForecast(
                    currentTemp = weather.temperature,
                    minTemp = weather.minTemperature,
                    maxTemp = weather.maxTemperature,
                    unitSymbol = unitSymbol
                )

                uiState = uiState.copy(
                    isLoading = false,
                    cityName = weather.cityName ?: city,   // 👈 сначала из API, потом введённое
                    temperatureText = "${weather.temperature.toInt()}$unitSymbol",
                    description = weather.description,
                    airQualityIndex = airQuality?.aqi,
                    airQualityText = airQuality?.aqi?.let { indexToText(it) },
                    feelsLikeText = feelsLikeText,
                    minTempText = minTempText,
                    maxTempText = maxTempText,
                    humidity = weather.humidity,
                    windSpeedText = windSpeedText,
                    pressure = weather.pressure,
                    dailyForecast = forecast,
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

                val feelsLikeText = "${weather.feelsLike.toInt()}$unitSymbol"
                val minTempText = weather.minTemperature?.let { "${it.toInt()}$unitSymbol" }
                val maxTempText = weather.maxTemperature?.let { "${it.toInt()}$unitSymbol" }
                val windSpeedText = "${weather.windSpeed} m/s"

                val forecast = buildDailyForecast(
                    currentTemp = weather.temperature,
                    minTemp = weather.minTemperature,
                    maxTemp = weather.maxTemperature,
                    unitSymbol = unitSymbol
                )

                uiState = uiState.copy(
                    isLoading = false,
                    cityName = weather.cityName       // 👈 главное: берём название города из API
                        ?: lastCityName              // если вдруг null — используем последнее
                        ?: uiState.cityName,         // или оставляем старое
                    temperatureText = "${weather.temperature.toInt()}$unitSymbol",
                    description = weather.description,
                    airQualityIndex = airQuality?.aqi,
                    airQualityText = airQuality?.aqi?.let { indexToText(it) },
                    feelsLikeText = feelsLikeText,
                    minTempText = minTempText,
                    maxTempText = maxTempText,
                    humidity = weather.humidity,
                    windSpeedText = windSpeedText,
                    pressure = weather.pressure,
                    dailyForecast = forecast,
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

    private fun buildDailyForecast(
        currentTemp: Double,
        minTemp: Double?,
        maxTemp: Double?,
        unitSymbol: String
    ): List<DailyForecastUiModel> {
        val baseMin = minTemp ?: currentTemp - 2
        val baseMax = maxTemp ?: currentTemp + 2

        return (0 until 5).map { index ->
            val label = when (index) {
                0 -> "Today"
                1 -> "Tomorrow"
                else -> "Day ${index + 1}"
            }

            val dayMin = (baseMin - 1 + index).toInt()
            val dayMax = (baseMax + index).toInt()

            DailyForecastUiModel(
                dayLabel = label,
                minTemp = "$dayMin$unitSymbol",
                maxTemp = "$dayMax$unitSymbol"
            )
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
