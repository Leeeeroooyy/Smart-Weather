package com.vadim_zinovev.smartweather.ui.currentweather

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vadim_zinovev.smartweather.R
import com.vadim_zinovev.smartweather.data.local.FavoriteCity
import com.vadim_zinovev.smartweather.data.local.FavoritesStorage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.max

private data class UiColors(
    val bg: Brush,
    val button: Color,
    val card: Color,
    val text: Color = Color.White,
    val indicatorSelected: Color = Color.White,
    val indicatorUnselected: Color = Color.White.copy(alpha = 0.4f)
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    val favoriteKeys = remember(favoriteCities) { favoriteCities.map { it.key } }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { max(favoriteCities.size + 1, 1) }
    )

    val colors = rememberUiColors()
    val scope = rememberCoroutineScope()

    var mainWeatherState by remember { mutableStateOf<CurrentWeatherUiState?>(null) }
    val cachedFavoriteStates = remember { mutableStateMapOf<String, CurrentWeatherUiState>() }
    var currentFavoriteKey by remember { mutableStateOf<String?>(null) }
    var pendingHomeUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(state, favoriteKeys, pagerState.currentPage, currentFavoriteKey) {
        val hasData = !state.isLoading && state.errorMessage == null && state.temperatureText != null
        if (!hasData) return@LaunchedEffect

        when {
            pagerState.currentPage == 0 || pendingHomeUpdate -> {
                mainWeatherState = state
                pendingHomeUpdate = false
            }

            pagerState.currentPage > 0 -> {
                val key = currentFavoriteKey ?: favoriteKeys.getOrNull(pagerState.currentPage - 1)
                if (key != null) cachedFavoriteStates[key] = state
            }
        }
    }

    LaunchedEffect(favoriteKeys) {
        val allowed = favoriteKeys.toSet()
        cachedFavoriteStates.keys.toList().forEach { key ->
            if (key !in allowed) cachedFavoriteStates.remove(key)
        }
        if (currentFavoriteKey != null && currentFavoriteKey !in allowed) {
            currentFavoriteKey = null
        }
    }

    LaunchedEffect(favoriteCities) {
        if (favoriteCities.isNotEmpty() &&
            !state.isLoading &&
            state.temperatureText == null &&
            state.errorMessage == null
        ) {
            val fav = favoriteCities.first()
            currentFavoriteKey = fav.key
            viewModel.loadWeatherForCityCoordinates(fav.title, fav.lat, fav.lon)
        }
    }

    LaunchedEffect(favoriteCities, pagerState) {
        if (favoriteCities.isEmpty()) return@LaunchedEffect

        var skipFirst = true
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (skipFirst) {
                    skipFirst = false
                    return@collect
                }
                if (page == 0) return@collect

                val fav = favoriteCities.getOrNull(page - 1) ?: return@collect
                currentFavoriteKey = fav.key
                viewModel.loadWeatherForCityCoordinates(fav.title, fav.lat, fav.lon)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrentWeatherTopBar(
                onSearchClick = onSearchClick,
                onFavoritesClick = onFavoritesClick,
                onSettingsClick = onSettingsClick,
                onMyLocationClick = {
                    pendingHomeUpdate = true
                    onMyLocationClick()
                },
                onHomeClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                buttonColor = colors.button,
                textColor = colors.text
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                CurrentWeatherStateContent(
                    state = state,
                    mainWeatherState = mainWeatherState,
                    pagerState = pagerState,
                    colors = colors,
                    favoriteKeys = favoriteKeys,
                    cachedFavoriteStates = cachedFavoriteStates,
                    currentFavoriteKey = currentFavoriteKey
                )
            }

            val showIndicator = pagerState.pageCount > 1 && (
                    mainWeatherState?.temperatureText != null ||
                            cachedFavoriteStates.isNotEmpty() ||
                            state.temperatureText != null
                    )

            if (showIndicator) {
                FavoriteCitiesPagerIndicator(
                    pageCount = pagerState.pageCount,
                    currentPage = pagerState.currentPage,
                    selectedColor = colors.indicatorSelected,
                    unselectedColor = colors.indicatorUnselected
                )
            }
        }
    }
}

@Composable
private fun rememberUiColors(): UiColors {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bg = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF121212)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF005B96), Color(0xFF008ECC)))
    }
    val button = if (isDark) Color(0xFF1E1E1E) else Color(0xFF003B66)
    val card = if (isDark) Color(0xFF1E1E1E) else Color.White.copy(alpha = 0.16f)
    return remember(isDark) { UiColors(bg = bg, button = button, card = card) }
}

@Composable
private fun ThemedCard(
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        content()
    }
}

@Composable
private fun CurrentWeatherTopBar(
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMyLocationClick: () -> Unit,
    onHomeClick: () -> Unit,
    buttonColor: Color,
    textColor: Color
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            SmallFloatingActionButton(
                onClick = { menuExpanded = true },
                containerColor = buttonColor,
                contentColor = textColor
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Search city") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    onClick = { menuExpanded = false; onSearchClick() }
                )
                DropdownMenuItem(
                    text = { Text("Favorites") },
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    onClick = { menuExpanded = false; onFavoritesClick() }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = { menuExpanded = false; onSettingsClick() }
                )
                DropdownMenuItem(
                    text = { Text("My location") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    onClick = { menuExpanded = false; onMyLocationClick() }
                )
            }
        }

        SmallFloatingActionButton(
            onClick = onHomeClick,
            containerColor = buttonColor,
            contentColor = textColor
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CurrentWeatherStateContent(
    state: CurrentWeatherUiState,
    mainWeatherState: CurrentWeatherUiState?,
    pagerState: PagerState,
    colors: UiColors,
    favoriteKeys: List<String>,
    cachedFavoriteStates: Map<String, CurrentWeatherUiState>,
    currentFavoriteKey: String?
) {
    val hasAnyCachedData =
        mainWeatherState?.temperatureText != null ||
                cachedFavoriteStates.isNotEmpty() ||
                state.temperatureText != null

    if (!hasAnyCachedData) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.text)
            }
            state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.errorMessage}", color = colors.text)
            }
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Find your city", color = colors.text)
            }
        }
        return
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val pageState: CurrentWeatherUiState? = if (page == 0) {
            if (pagerState.currentPage == 0) state else mainWeatherState
        } else {
            val key = favoriteKeys.getOrNull(page - 1)
            if (key == null) null
            else if (pagerState.currentPage == page && currentFavoriteKey == key) state
            else cachedFavoriteStates[key]
        }

        when {
            pageState != null -> WeatherCardContent(
                state = pageState,
                colors = colors,
                modifier = Modifier.fillMaxSize()
            )
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (pagerState.currentPage == page && state.isLoading) {
                    CircularProgressIndicator(color = colors.text)
                } else {
                    Text(text = "No data yet", color = colors.text)
                }
            }
        }
    }
}

@Composable
private fun FavoriteCitiesPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    selectedColor: Color,
    unselectedColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val selected = currentPage == index
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (selected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (selected) selectedColor else unselectedColor)
            )
        }
    }
}

@DrawableRes
private fun weatherBackgroundRes(iconCode: String?): Int {
    val base = iconCode?.take(2)
    return when (base) {
        "01" -> R.drawable.bg_weather_clear
        "02" -> R.drawable.bg_weather_few_clouds
        "03", "04" -> R.drawable.bg_weather_cloudy
        "09", "10" -> R.drawable.bg_weather_rain
        "11" -> R.drawable.bg_weather_storm
        "13" -> R.drawable.bg_weather_snow
        "50" -> R.drawable.bg_weather_mist
        else -> R.drawable.bg_weather_cloudy
    }
}

@Composable
private fun WeatherCardContent(
    state: CurrentWeatherUiState,
    colors: UiColors,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                text = state.cityName.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                color = colors.text
            )

            Spacer(modifier = Modifier.height(8.dp))

            val bgRes = weatherBackgroundRes(state.iconCode)

            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(bgRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(state.minTempText ?: "", color = colors.text)
                        Text(state.maxTempText ?: "", color = colors.text)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = state.temperatureText.orEmpty(),
                        style = MaterialTheme.typography.displaySmall,
                        color = colors.text
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = state.description.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.text
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.feelsLikeText ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.text
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.dailyForecast.isNotEmpty()) {
                ForecastBlock(
                    forecast = state.dailyForecast,
                    colors = colors
                )
            }

            val hasDetails = state.humidity != null ||
                    state.windSpeedText != null ||
                    state.pressure != null

            if (hasDetails) {
                ThemedCard(
                    containerColor = colors.card,
                    contentColor = colors.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Details", style = MaterialTheme.typography.titleMedium)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            state.humidity?.let {
                                DetailItemWithBar(
                                    modifier = Modifier.weight(1f),
                                    label = "Humidity",
                                    valueText = "$it%",
                                    fraction = (it.coerceIn(0, 100)) / 100f,
                                    textColor = colors.text
                                )
                            }
                            state.windSpeedText?.let {
                                DetailItemWithBar(
                                    modifier = Modifier.weight(1f),
                                    label = "Wind",
                                    valueText = it,
                                    fraction = 0.5f,
                                    textColor = colors.text
                                )
                            }
                            state.pressure?.let {
                                val fraction = ((it - 950).coerceIn(0, 100)) / 100f
                                DetailItemWithBar(
                                    modifier = Modifier.weight(1f),
                                    label = "Pressure",
                                    valueText = "$it hPa",
                                    fraction = fraction,
                                    textColor = colors.text
                                )
                            }
                        }
                    }
                }
            }

            ThemedCard(
                containerColor = colors.card,
                contentColor = colors.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Air quality", style = MaterialTheme.typography.titleMedium)
                    if (state.airQualityIndex != null && state.airQualityText != null) {
                        Text("AQI: ${state.airQualityIndex}")
                        Text(state.airQualityText)
                    } else {
                        Text("No data")
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastBlock(
    forecast: List<DailyForecastUiModel>,
    colors: UiColors
) {
    ThemedCard(
        containerColor = colors.card,
        contentColor = colors.text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("5-day forecast", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                forecast.take(5).forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(day.dayLabel, style = MaterialTheme.typography.bodySmall)
                        Text(day.maxTemp, style = MaterialTheme.typography.bodyMedium)
                        Text(day.minTemp, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItemWithBar(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String,
    fraction: Float,
    textColor: Color
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(valueText, color = textColor)
        Text(label, style = MaterialTheme.typography.bodySmall, color = textColor)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
