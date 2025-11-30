package com.vadim_zinovev.smartweather.data.remote.dto

import com.squareup.moshi.Json

data class CityGeocodingDto(
    @Json(name = "name")
    val name: String,
    @Json(name = "country")
    val country: String,
    @Json(name = "lat")
    val latitude: Double,
    @Json(name = "lon")
    val longitude: Double
)