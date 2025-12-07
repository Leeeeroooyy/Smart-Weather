package com.vadim_zinovev.smartweather.ui.currentweather

data class CurrentWeatherUiState(
    val isLoading: Boolean = false,
    val cityName: String? = null,
    val temperatureText: String? = null,
    val airQualityIndex: Int? = null,
    val airQualityText: String? = null,
    val description: String? = null,
    val errorMessage: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val feelsLikeText: String? = null,
    val minTempText: String? = null,
    val maxTempText: String? = null,
    val humidity: Int? = null,
    val windSpeedText: String? = null,
    val pm25: Double? = null,
    val pm10: Double? = null,
    val o3: Double? = null
)
