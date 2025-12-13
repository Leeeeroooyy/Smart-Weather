package com.vadim_zinovev.smartweather.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim_zinovev.smartweather.data.local.FavoriteCity
import com.vadim_zinovev.smartweather.data.local.FavoritesStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesStorage: FavoritesStorage
) : ViewModel() {

    val favoriteCities: StateFlow<List<FavoriteCity>> =
        favoritesStorage.favoriteCities.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onRemoveCity(cityKey: String) {
        viewModelScope.launch {
            favoritesStorage.remove(cityKey)
        }
    }
}
