package com.nizarmah.igatha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nizarmah.igatha.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val _disasterDetectionEnabled = MutableStateFlow(
        // Load the setting from user settings
        UserSettings.isDisasterDetectionEnabled(app)
    )
    val disasterDetectionEnabled: StateFlow<Boolean> = _disasterDetectionEnabled

    fun setDisasterDetectionEnabled(enabled: Boolean) {
        _disasterDetectionEnabled.value = enabled
        // Save the setting to user settings
        UserSettings.setDisasterDetectionEnabled(getApplication(), enabled)
    }
}
