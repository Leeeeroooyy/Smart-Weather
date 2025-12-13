package com.vadim_zinovev.smartweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vadim_zinovev.smartweather.data.local.FavoriteCity
import com.vadim_zinovev.smartweather.data.local.FavoritesStorage
import com.vadim_zinovev.smartweather.ui.citydetail.CityDetailScreen
import com.vadim_zinovev.smartweather.ui.citysearch.CitySearchScreen
import com.vadim_zinovev.smartweather.ui.currentweather.CurrentWeatherScreen
import com.vadim_zinovev.smartweather.ui.currentweather.CurrentWeatherViewModel
import com.vadim_zinovev.smartweather.ui.currentweather.CurrentWeatherViewModelFactory
import com.vadim_zinovev.smartweather.ui.favorites.FavoritesScreen
import com.vadim_zinovev.smartweather.ui.settings.SettingsScreen
import com.vadim_zinovev.smartweather.ui.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.CurrentWeather.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CurrentWeather.route) { backStackEntry ->
            val context = LocalContext.current
            val currentWeatherViewModel: CurrentWeatherViewModel = viewModel(
                factory = CurrentWeatherViewModelFactory(context)
            )

            val selectedCityName =
                backStackEntry.savedStateHandle.get<String>("selectedCityName")
            val selectedLat =
                backStackEntry.savedStateHandle.get<Double>("selectedLat")
            val selectedLon =
                backStackEntry.savedStateHandle.get<Double>("selectedLon")

            if (selectedCityName != null && selectedLat != null && selectedLon != null) {
                LaunchedEffect(selectedCityName, selectedLat, selectedLon) {
                    currentWeatherViewModel.loadWeatherForCityCoordinates(
                        cityName = selectedCityName,
                        latitude = selectedLat,
                        longitude = selectedLon
                    )
                    backStackEntry.savedStateHandle["selectedCityName"] = null
                    backStackEntry.savedStateHandle["selectedLat"] = null
                    backStackEntry.savedStateHandle["selectedLon"] = null
                }
            }

            CurrentWeatherScreen(
                viewModel = currentWeatherViewModel,
                onSearchClick = { navController.navigate(Screen.CitySearch.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onMyLocationClick = { currentWeatherViewModel.loadWeatherForCurrentLocation() },
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) }
            )
        }

        composable(Screen.CitySearch.route) {
            CitySearchScreen(
                onCitySelected = { city ->
                    val currentWeatherEntry =
                        navController.getBackStackEntry(Screen.CurrentWeather.route)

                    currentWeatherEntry.savedStateHandle["selectedCityName"] =
                        "${city.name}, ${city.country}"
                    currentWeatherEntry.savedStateHandle["selectedLat"] = city.latitude
                    currentWeatherEntry.savedStateHandle["selectedLon"] = city.longitude

                    navController.popBackStack(
                        route = Screen.CurrentWeather.route,
                        inclusive = false
                    )
                },
                showAddButton = false,
                onAddFavorite = null
            )
        }

        composable(Screen.CitySearchFavorites.route) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val favoritesStorage = FavoritesStorage(context.applicationContext)

            CitySearchScreen(
                onCitySelected = { city ->
                    scope.launch {
                        favoritesStorage.add(
                            FavoriteCity(
                                name = city.name,
                                country = city.country,
                                lat = city.latitude,
                                lon = city.longitude
                            )
                        )

                        val currentWeatherEntry =
                            navController.getBackStackEntry(Screen.CurrentWeather.route)

                        currentWeatherEntry.savedStateHandle["selectedCityName"] =
                            "${city.name}, ${city.country}"
                        currentWeatherEntry.savedStateHandle["selectedLat"] = city.latitude
                        currentWeatherEntry.savedStateHandle["selectedLon"] = city.longitude

                        navController.popBackStack(
                            route = Screen.CurrentWeather.route,
                            inclusive = false
                        )
                    }
                },
                showAddButton = false,
                onAddFavorite = null
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onCitySelected = { city ->
                    val currentWeatherEntry =
                        navController.getBackStackEntry(Screen.CurrentWeather.route)

                    currentWeatherEntry.savedStateHandle["selectedCityName"] = city.title
                    currentWeatherEntry.savedStateHandle["selectedLat"] = city.lat
                    currentWeatherEntry.savedStateHandle["selectedLon"] = city.lon

                    navController.popBackStack(
                        route = Screen.CurrentWeather.route,
                        inclusive = false
                    )
                },
                onSearchClick = {
                    navController.navigate(Screen.CitySearchFavorites.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(Screen.CityDetail.route) { backStackEntry ->
            val cityId = backStackEntry.arguments
                ?.getString("cityId")
                ?.toLongOrNull() ?: -1L
            CityDetailScreen(cityId = cityId)
        }
    }
}
