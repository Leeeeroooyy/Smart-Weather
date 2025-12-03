package com.vadim_zinovev.smartweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vadim_zinovev.smartweather.ui.citydetail.CityDetailScreen
import com.vadim_zinovev.smartweather.ui.citysearch.CitySearchScreen
import com.vadim_zinovev.smartweather.ui.currentweather.CurrentWeatherScreen
import com.vadim_zinovev.smartweather.ui.currentweather.CurrentWeatherViewModel
import com.vadim_zinovev.smartweather.ui.currentweather.CurrentWeatherViewModelFactory
import com.vadim_zinovev.smartweather.ui.favorites.FavoritesScreen
import com.vadim_zinovev.smartweather.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CurrentWeather.route
    ) {
        composable(Screen.CurrentWeather.route) { backStackEntry ->
            val context = LocalContext.current
            val currentWeatherViewModel: CurrentWeatherViewModel = viewModel(
                factory = CurrentWeatherViewModelFactory(context)
            )

            val selectedCityName =
                backStackEntry.savedStateHandle.get<String>("selectedCityName")

            if (selectedCityName != null) {
                LaunchedEffect(selectedCityName) {
                    currentWeatherViewModel.loadWeatherForCity(selectedCityName)
                    backStackEntry.savedStateHandle["selectedCityName"] = null
                }
            }

            CurrentWeatherScreen(
                viewModel = currentWeatherViewModel,
                onSearchClick = { navController.navigate(Screen.CitySearch.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.CitySearch.route) {
            CitySearchScreen(
                onCitySelected = { cityName ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedCityName", cityName)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen()
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
