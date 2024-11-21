package com.nizarmah.igatha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nizarmah.igatha.util.SettingsManager
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    val disasterDetectionEnabled: StateFlow<Boolean> = SettingsManager.disasterDetectionEnabled

    fun setDisasterDetectionEnabled(enabled: Boolean) {
        SettingsManager.setDisasterDetectionEnabled(enabled)
    }
}
