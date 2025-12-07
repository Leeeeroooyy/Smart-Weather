package com.vadim_zinovev.smartweather.ui.currentweather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vadim_zinovev.smartweather.data.location.LocationProvider
import com.vadim_zinovev.smartweather.data.remote.api.WeatherApiFactory
import com.vadim_zinovev.smartweather.data.repository.SettingsRepositoryImpl
import com.vadim_zinovev.smartweather.data.repository.WeatherRepositoryImpl
import com.vadim_zinovev.smartweather.domain.repository.SettingsRepository
import com.vadim_zinovev.smartweather.domain.repository.WeatherRepository

class CurrentWeatherViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrentWeatherViewModel::class.java)) {
            val weatherRepository: WeatherRepository =
                WeatherRepositoryImpl(WeatherApiFactory.create())
            val settingsRepository: SettingsRepository =
                SettingsRepositoryImpl(context.applicationContext)
            val locationProvider = LocationProvider(context.applicationContext)

            @Suppress("UNCHECKED_CAST")
            return CurrentWeatherViewModel(
                weatherRepository = weatherRepository,
                settingsRepository = settingsRepository,
                locationProvider = locationProvider
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
