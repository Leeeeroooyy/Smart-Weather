package com.vadim_zinovev.smartweather.ui.currentweather

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun CurrentWeatherScreen(
    viewModel: CurrentWeatherViewModel,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                viewModel.loadWeatherForCurrentLocation()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onSearchClick) {
                Text(text = "Search city")
            }

            Button(onClick = onSettingsClick) {
                Text(text = "Settings")
            }

            Button(
                onClick = {
                    val hasFine = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasCoarse = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFine || hasCoarse) {
                        viewModel.loadWeatherForCurrentLocation()
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            ) {
                Text("My location")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
