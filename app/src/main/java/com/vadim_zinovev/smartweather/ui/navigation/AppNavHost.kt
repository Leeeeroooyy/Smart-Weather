package com.vadim_zinovev.smartweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.vadim_zinovev.smartweather.ui.currentweather.CurrentWeatherScreen
import com.vadim_zinovev.smartweather.ui.citysearch.CitySearchScreen
import com.vadim_zinovev.smartweather.ui.citydetail.CityDetailScreen
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
        composable(Screen.CurrentWeather.route) {
            CurrentWeatherScreen()
        }

        composable(Screen.CitySearch.route) {
            CitySearchScreen()
        }

        composable(
            route = Screen.CityDetail.route,
            arguments = listOf(
                navArgument("cityId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getLong("cityId") ?: -1L
            CityDetailScreen(cityId = cityId)
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
