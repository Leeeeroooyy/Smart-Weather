package com.vadim_zinovev.smartweather.ui.navigation

sealed class Screen(val route: String) {
    object CurrentWeather : Screen("current_weather")
    object CitySearch : Screen("city_search")
    object CitySearchFavorites : Screen("city_search_favorites")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object CityDetail : Screen("city_detail/{cityId}") {
    }
}
