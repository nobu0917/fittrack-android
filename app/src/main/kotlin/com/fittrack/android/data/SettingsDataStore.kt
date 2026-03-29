package com.fittrack.android.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class WeightUnit(val label: String, val suffix: String) {
    KG("kg", "kg"),
    LBS("lbs", "lbs");

    fun convert(kg: Float): Float = when (this) {
        KG -> kg
        LBS -> kg * 2.20462f
    }

    fun toKg(value: Float): Float = when (this) {
        KG -> value
        LBS -> value / 2.20462f
    }
}

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fittrack_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _weightUnit = MutableStateFlow(loadWeightUnit())
    val weightUnit: StateFlow<WeightUnit> = _weightUnit.asStateFlow()

    private fun loadThemeMode(): String = prefs.getString("theme_mode", "system") ?: "system"

    private fun loadWeightUnit(): WeightUnit {
        val value = prefs.getString("weight_unit", "KG") ?: "KG"
        return try { WeightUnit.valueOf(value) } catch (_: Exception) { WeightUnit.KG }
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _themeMode.value = mode
    }

    fun setWeightUnit(unit: WeightUnit) {
        prefs.edit().putString("weight_unit", unit.name).apply()
        _weightUnit.value = unit
    }
}
