package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.model.AngleMode
import com.example.data.history.HistoryDatabase
import com.example.data.history.HistoryRepository
import com.example.data.preferences.AppSettings
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val historyRepository = HistoryRepository(HistoryDatabase.getInstance(application).historyDao())

    val settings: StateFlow<AppSettings> = preferencesManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun setAngleMode(mode: AngleMode) {
        viewModelScope.launch {
            preferencesManager.setAngleMode(mode)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setHapticFeedback(enabled)
        }
    }

    fun setButtonSound(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setButtonSound(enabled)
        }
    }

    fun setThousandsSeparator(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setThousandsSeparator(enabled)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }
}
