package com.nizarmah.igatha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nizarmah.igatha.UserSettings
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    val disasterDetectionEnabled: StateFlow<Boolean> = UserSettings.disasterDetectionEnabled

    fun setDisasterDetectionEnabled(enabled: Boolean) {
        UserSettings.setDisasterDetectionEnabled(enabled)
    }
}
