package com.fittrack.android.ui.settings

import androidx.lifecycle.ViewModel
import com.fittrack.android.data.SettingsDataStore
import com.fittrack.android.data.WeightUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val themeMode: StateFlow<String> = settingsDataStore.themeMode
    val weightUnit: StateFlow<WeightUnit> = settingsDataStore.weightUnit

    fun setThemeMode(mode: String) {
        settingsDataStore.setThemeMode(mode)
    }

    fun setWeightUnit(unit: WeightUnit) {
        settingsDataStore.setWeightUnit(unit)
    }
}
