package com.nizarmah.igatha

import android.app.Application
import com.nizarmah.igatha.util.PermissionsManager
import com.nizarmah.igatha.util.SettingsManager

class IgathaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        SettingsManager.init(this)
        PermissionsManager.init(this)
    }
}
