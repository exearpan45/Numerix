package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.calculator.model.AngleMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "numerix_preferences")

data class AppSettings(
    val themeMode: String = "system",
    val angleMode: AngleMode = AngleMode.DEG,
    val hapticFeedback: Boolean = true,
    val buttonSound: Boolean = false,
    val thousandsSeparator: Boolean = true
)

class PreferencesManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ANGLE_MODE = stringPreferencesKey("angle_mode")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val BUTTON_SOUND = booleanPreferencesKey("button_sound")
        val THOUSANDS_SEPARATOR = booleanPreferencesKey("thousands_separator")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE] ?: "system",
            angleMode = if (prefs[Keys.ANGLE_MODE] == "RAD") AngleMode.RAD else AngleMode.DEG,
            hapticFeedback = prefs[Keys.HAPTIC_FEEDBACK] ?: true,
            buttonSound = prefs[Keys.BUTTON_SOUND] ?: false,
            thousandsSeparator = prefs[Keys.THOUSANDS_SEPARATOR] ?: true
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setAngleMode(mode: AngleMode) {
        context.dataStore.edit { it[Keys.ANGLE_MODE] = mode.name }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setButtonSound(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BUTTON_SOUND] = enabled }
    }

    suspend fun setThousandsSeparator(enabled: Boolean) {
        context.dataStore.edit { it[Keys.THOUSANDS_SEPARATOR] = enabled }
    }
}
