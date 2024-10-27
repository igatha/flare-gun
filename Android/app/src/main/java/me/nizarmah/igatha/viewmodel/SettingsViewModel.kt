package me.nizarmah.igatha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _disasterDetectionEnabled = MutableStateFlow(false)
    val disasterDetectionEnabled: StateFlow<Boolean> = _disasterDetectionEnabled

    private val sharedPreferences = application.getSharedPreferences("settings", Application.MODE_PRIVATE)

    fun setDisasterDetectionEnabled(enabled: Boolean) {
        _disasterDetectionEnabled.value = enabled
        // Save the setting to SharedPreferences
        viewModelScope.launch {
            sharedPreferences.edit().putBoolean("disasterDetectionEnabled", enabled).apply()
        }
    }

    init {
        // Load the setting from SharedPreferences
        val enabled = sharedPreferences.getBoolean("disasterDetectionEnabled", false)
        _disasterDetectionEnabled.value = enabled
    }
}
