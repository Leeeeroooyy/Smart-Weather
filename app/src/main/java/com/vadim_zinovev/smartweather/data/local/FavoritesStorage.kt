package com.vadim_zinovev.smartweather.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore(name = "favorites_store")

class FavoritesStorage(private val context: Context) {

    private val keyFavorites = stringPreferencesKey("favorite_cities_v2")

    val favoriteCityKeys: Flow<List<String>> = context.favoritesDataStore.data.map { prefs ->
        prefs[keyFavorites]
            ?.split("\n")
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    val favoriteCities: Flow<List<FavoriteCity>> = favoriteCityKeys.map { keys ->
        keys.mapNotNull { FavoriteCity.fromKey(it) }
    }

    suspend fun add(city: FavoriteCity) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[keyFavorites]
                ?.split("\n")
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()

            val k = city.key
            if (!current.contains(k)) {
                current.add(k)
                prefs[keyFavorites] = current.joinToString("\n")
            }
        }
    }

    suspend fun remove(cityKey: String) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[keyFavorites]
                ?.split("\n")
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()

            current.removeAll { it == cityKey }
            prefs[keyFavorites] = current.joinToString("\n")
        }
    }

    suspend fun clear() {
        context.favoritesDataStore.edit { prefs ->
            prefs.remove(keyFavorites)
        }
    }
}
