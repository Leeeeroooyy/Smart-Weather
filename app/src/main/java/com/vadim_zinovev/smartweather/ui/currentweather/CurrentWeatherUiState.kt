package com.vadim_zinovev.smartweather.ui.currentweather

data class CurrentWeatherUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val cityName: String? = null,
    val temperatureText: String? = null,
    val description: String? = null,
    val airQualityIndex: Int? = null,
    val airQualityText: String? = null,
    val feelsLikeText: String? = null,
    val minTempText: String? = null,
    val maxTempText: String? = null,
    val humidity: Int? = null,
    val windSpeedText: String? = null,
    val pressure: Int? = null,
    val dailyForecast: List<DailyForecastUiModel> = emptyList()
)

data class DailyForecastUiModel(
    val dayLabel: String,
    val minTemp: String,
    val maxTemp: String
)
