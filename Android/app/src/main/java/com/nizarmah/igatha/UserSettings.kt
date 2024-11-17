package com.nizarmah.igatha

import android.content.Context
import android.content.SharedPreferences

object UserSettings {
    fun isDisasterDetectionEnabled(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(Constants.DISASTER_DETECTION_ENABLED_KEY, false)
    }

    fun setDisasterDetectionEnabled(context: Context, enabled: Boolean) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putBoolean(Constants.DISASTER_DETECTION_ENABLED_KEY, enabled).apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            Constants.SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
    }
}
