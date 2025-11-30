package com.vadim_zinovev.smartweather.data.remote.api

import com.vadim_zinovev.smartweather.data.remote.dto.AirQualityResponseDto
import com.vadim_zinovev.smartweather.data.remote.dto.CityGeocodingDto
import com.vadim_zinovev.smartweather.data.remote.dto.CurrentWeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCityName(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): CurrentWeatherResponseDto

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): CurrentWeatherResponseDto

    @GET("data/2.5/air_pollution")
    suspend fun getAirQualityByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): AirQualityResponseDto

    @GET("geo/1.0/direct")
    suspend fun searchCitiesByName(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<CityGeocodingDto>
}