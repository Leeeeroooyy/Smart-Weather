package com.vadim_zinovev.smartweather.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim_zinovev.smartweather.domain.model.TemperatureUnit
import com.vadim_zinovev.smartweather.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val selectedUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isLoading: Boolean = true
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            val unit = settingsRepository.observeTemperatureUnit().first()
            _uiState.value = SettingsUiState(
                selectedUnit = unit,
                isLoading = false
            )
        }
    }

    fun onUnitSelected(unit: TemperatureUnit) {
        if (_uiState.value.selectedUnit == unit) return

        _uiState.value = _uiState.value.copy(selectedUnit = unit)

        viewModelScope.launch {
            settingsRepository.setTemperatureUnit(unit)
        }
    }
}
