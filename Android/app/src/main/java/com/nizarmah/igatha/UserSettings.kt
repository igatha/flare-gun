package com.nizarmah.igatha

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSettings {
    private val _disasterDetectionEnabled = MutableStateFlow(true)
    val disasterDetectionEnabled: StateFlow<Boolean> = _disasterDetectionEnabled.asStateFlow()

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = getSharedPreferences(context)

        // Initialize the StateFlow with the current value
        val enabled = sharedPreferences.getBoolean(Constants.DISASTER_DETECTION_ENABLED_KEY, true)
        _disasterDetectionEnabled.value = enabled

        // Listen for changes in SharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener { prefs, key ->
            if (key == Constants.DISASTER_DETECTION_ENABLED_KEY) {
                val newValue = prefs.getBoolean(key, true)
                _disasterDetectionEnabled.value = newValue
            }
        }
    }

    fun setDisasterDetectionEnabled(enabled: Boolean) {
        _disasterDetectionEnabled.value = enabled

        sharedPreferences.edit().putBoolean(
            Constants.DISASTER_DETECTION_ENABLED_KEY, enabled).apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            Constants.SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
    }
}
