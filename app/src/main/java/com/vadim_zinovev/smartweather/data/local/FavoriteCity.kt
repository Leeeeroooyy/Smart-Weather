package com.vadim_zinovev.smartweather.data.local

data class FavoriteCity(
    val name: String,
    val country: String?,
    val lat: Double,
    val lon: Double
) {
    val key: String get() = "${name}|${country.orEmpty()}|$lat|$lon"
    val title: String get() = if (country.isNullOrBlank()) name else "$name, $country"

    companion object {
        fun fromKey(key: String): FavoriteCity? {
            val parts = key.split("|")
            if (parts.size != 4) return null
            val name = parts[0]
            val country = parts[1].ifBlank { null }
            val lat = parts[2].toDoubleOrNull() ?: return null
            val lon = parts[3].toDoubleOrNull() ?: return null
            return FavoriteCity(name = name, country = country, lat = lat, lon = lon)
        }
    }
}
