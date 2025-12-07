package com.vadim_zinovev.smartweather.ui.currentweather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CurrentWeatherScreen(
    viewModel: CurrentWeatherViewModel,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMyLocationClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2D4BFF),
                            Color(0xFF96B4FF)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Smart Weather",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(
                                color = Color.White
                            )
                        }

                        state.errorMessage != null -> {
                            Text(
                                text = state.errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        else -> {
                            WeatherContent(state = state)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MainActionButton(
                        text = "Search",
                        onClick = onSearchClick,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(0.dp).padding(horizontal = 4.dp))
                    MainActionButton(
                        text = "Settings",
                        onClick = onSettingsClick,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(0.dp).padding(horizontal = 4.dp))
                    MainActionButton(
                        text = "My location",
                        onClick = onMyLocationClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = onFavoritesClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1F2A72),
                        contentColor = Color.White
                    )
                ) {
                    Text("Favorites")
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(
    state: CurrentWeatherUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.cityName ?: "",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = state.temperatureText ?: "",
            fontSize = 40.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = state.description?.replaceFirstChar { it.uppercaseChar() } ?: "",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (state.airQualityIndex != null && state.airQualityText != null) {
            AirQualityCard(
                index = state.airQualityIndex,
                description = state.airQualityText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MainActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1F2A72),
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}

@Composable
private fun AirQualityCard(
    index: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Air quality",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Index $index",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
