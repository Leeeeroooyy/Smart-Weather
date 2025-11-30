package com.vadim_zinovev.smartweather.ui.currentweather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CurrentWeatherScreen(
    viewModel: CurrentWeatherViewModel = viewModel()
) {
    val state = viewModel.uiState

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.errorMessage != null -> {
                Text(text = "Error: ${state.errorMessage}")
            }
            state.temperatureText != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.cityName ?: "")
                    Text(text = state.temperatureText)
                    Text(text = state.description ?: "")
                }
            }
            else -> {
                Text(text = "No data")
            }
        }
    }
}
