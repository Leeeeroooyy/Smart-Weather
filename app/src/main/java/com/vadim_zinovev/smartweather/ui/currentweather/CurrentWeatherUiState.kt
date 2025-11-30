package com.vadim_zinovev.smartweather.ui.currentweather

data class CurrentWeatherUiState(
    val isLoading: Boolean = false,
    val cityName: String? = null,
    val temperatureText: String? = null,
    val description: String? = null,
    val errorMessage: String? = null
)