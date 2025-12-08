package com.vadim_zinovev.smartweather.ui.currentweather

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vadim_zinovev.smartweather.data.local.FavoritesStorage
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentWeatherScreen(
    viewModel: CurrentWeatherViewModel,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMyLocationClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    val favoritesStorage = remember { FavoritesStorage(context.applicationContext) }
    val favoriteCities by favoritesStorage.favoriteCities.collectAsState(initial = emptyList())

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { max(favoriteCities.size, 1) }
    )

    var lastRequestedCity by remember { mutableStateOf<String?>(null) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(favoriteCities, pagerState.currentPage, state.cityName) {
        if (favoriteCities.isNotEmpty()) {
            val index = pagerState.currentPage.coerceIn(favoriteCities.indices)
            val city = favoriteCities[index]
            val cityInFavorites = state.cityName?.let { it in favoriteCities } ?: false
            if (state.cityName == null || cityInFavorites) {
                if (city != lastRequestedCity) {
                    lastRequestedCity = city
                    viewModel.loadWeatherForCity(city)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5B86E5),
                        Color(0xFF36D1DC)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = Color.White)
                }

                state.errorMessage != null -> {
                    Text(
                        text = "Error: ${state.errorMessage}",
                        color = Color.White
                    )
                }

                state.temperatureText != null -> {
                    if (favoriteCities.isNotEmpty()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            WeatherCardContent(
                                state = state,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        WeatherCardContent(
                            state = state,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                }

                else -> {
                    Text(
                        text = "No data",
                        color = Color.White
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 4.dp, y = 4.dp)
        ) {
            Button(
                onClick = { isMenuExpanded = true },
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF324A8A),
                    contentColor = Color.White
                ),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Text(
                    text = "☰",
                    fontSize = 18.sp
                )
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Search city") },
                    onClick = {
                        isMenuExpanded = false
                        onSearchClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Favorites") },
                    onClick = {
                        isMenuExpanded = false
                        onFavoritesClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("My location") },
                    onClick = {
                        isMenuExpanded = false
                        onMyLocationClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        isMenuExpanded = false
                        onSettingsClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun WeatherCardContent(
    state: CurrentWeatherUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = state.cityName.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = state.temperatureText.orEmpty(),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = state.description.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

        if (state.airQualityIndex != null && state.airQualityText != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.18f),
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Air quality",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "AQI: ${state.airQualityIndex}")
                    Text(text = state.airQualityText)
                }
            }
        }
    }
}
