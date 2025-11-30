package com.vadim_zinovev.smartweather.domain.model

data class City(
    val id: Long,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean
)