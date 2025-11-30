package com.vadim_zinovev.smartweather.ui.navigation

sealed class Screen(val route: String) {

    object CurrentWeather : Screen("current_weather")

    object CitySearch : Screen("city_search")

    object Favorites : Screen("favorites")

    object CityDetail : Screen("city_detail/{cityId}") {
        fun createRoute(cityId: Long): String = "city_detail/$cityId"
    }

    object Settings : Screen("settings")
}